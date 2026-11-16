package com.streamora.streamora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import com.streamora.streamora.dto.playlists.PlaylistDetailsResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.infrastructure.liquidsoap.LiquidsoapFileWriter;
import com.streamora.streamora.infrastructure.liquidsoap.LiquidsoapPlaylistSelector;
import com.streamora.streamora.infrastructure.liquidsoap.LiquidsoapScheduleGenerator;
import com.streamora.streamora.infrastructure.liquidsoap.LiquidsoapSourceGenerator;
import com.streamora.streamora.infrastructure.liquidsoap.M3uContentGenerator;
import com.streamora.streamora.infrastructure.liquidsoap.M3uContentReader;
import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapApplyInProgressException;
import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapFileException;
import com.streamora.streamora.infrastructure.liquidsoap.interfaces.LiquidsoapProcessControl;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapSelection;

@ExtendWith(MockitoExtension.class)
class LiquidsoapServiceTests {

	@Mock
	private PlaylistService playlistService;

	@Mock
	private ScheduleService scheduleService;

	@Mock
	private M3uContentGenerator m3uContentGenerator;

	@Mock
	private M3uContentReader m3uContentReader;

	@Mock
	private LiquidsoapFileWriter fileWriter;

	@Mock
	private LiquidsoapPlaylistSelector playlistSelector;

	@Mock
	private LiquidsoapSourceGenerator sourceGenerator;

	@Mock
	private LiquidsoapScheduleGenerator scheduleGenerator;

	@Mock
	private LiquidsoapProcessControl processControl;

	private LiquidsoapService liquidsoapService;

	@BeforeEach
	void setUp() {
		liquidsoapService = new LiquidsoapService(
			playlistService,
			scheduleService,
			m3uContentGenerator,
			m3uContentReader,
			fileWriter,
			playlistSelector,
			sourceGenerator,
			scheduleGenerator,
			processControl
		);
	}

	@Test
	void writesM3uBeforeAssociatingItWithPlaylist() {
		Playlist playlist = new Playlist();
		when(m3uContentGenerator.fromMedia("opening.mp3")).thenReturn("#EXTM3U\n/media.mp3\n");
		when(playlistService.associateM3uFileName("playlist-uuid", "playlist.m3u"))
			.thenReturn(playlist);

		Playlist returnedPlaylist = liquidsoapService.generateM3uFromMedia(
			"playlist-uuid",
			"playlist.m3u",
			"opening.mp3"
		);

		assertThat(returnedPlaylist).isSameAs(playlist);
		verify(fileWriter).writeM3u("playlist.m3u", "#EXTM3U\n/media.mp3\n");
		verify(playlistService).associateM3uFileName("playlist-uuid", "playlist.m3u");
	}

	@Test
	void coordinatesSeparateSourceAndScheduleFiles() {
		LiquidsoapSelection selection = new LiquidsoapSelection(List.of(), List.of());
		when(playlistService.findBasePlaylists()).thenReturn(List.of());
		when(scheduleService.findAllSchedules()).thenReturn(List.of());
		when(playlistSelector.select(List.of(), List.of())).thenReturn(selection);
		when(sourceGenerator.generate(selection)).thenReturn("sources");
		when(scheduleGenerator.generate(selection)).thenReturn("schedule");

		liquidsoapService.generateConfiguration();

		verify(fileWriter).writeConfiguration(LiquidsoapService.SOURCES_FILE_NAME, "sources");
		verify(fileWriter).writeConfiguration(LiquidsoapService.SCHEDULE_FILE_NAME, "schedule");
	}

	@Test
	void appliesGeneratedConfigurationBeforeRestartingLiquidsoap() {
		LiquidsoapSelection selection = new LiquidsoapSelection(List.of(), List.of());
		when(playlistService.findBasePlaylists()).thenReturn(List.of());
		when(scheduleService.findAllSchedules()).thenReturn(List.of());
		when(playlistSelector.select(List.of(), List.of())).thenReturn(selection);
		when(sourceGenerator.generate(selection)).thenReturn("sources");
		when(scheduleGenerator.generate(selection)).thenReturn("schedule");

		liquidsoapService.applyConfiguration();

		InOrder applyOrder = inOrder(fileWriter, processControl);
		applyOrder.verify(fileWriter)
			.writeConfiguration(LiquidsoapService.SOURCES_FILE_NAME, "sources");
		applyOrder.verify(fileWriter)
			.writeConfiguration(LiquidsoapService.SCHEDULE_FILE_NAME, "schedule");
		applyOrder.verify(processControl).restartAndAwaitRecovery();
	}

	@Test
	void doesNotRestartLiquidsoapWhenConfigurationGenerationFails() {
		when(playlistService.findBasePlaylists())
			.thenThrow(new LiquidsoapFileException("Configuration generation failed"));

		assertThatThrownBy(() -> liquidsoapService.applyConfiguration())
			.isInstanceOf(LiquidsoapFileException.class)
			.hasMessage("Configuration generation failed");

		verify(processControl, never()).restartAndAwaitRecovery();
	}

	@Test
	void rejectsConcurrentConfigurationApplication() throws Exception {
		LiquidsoapSelection selection = new LiquidsoapSelection(List.of(), List.of());
		CountDownLatch restartStarted = new CountDownLatch(1);
		CountDownLatch releaseRestart = new CountDownLatch(1);
		AtomicReference<Throwable> asynchronousFailure = new AtomicReference<>();
		when(playlistService.findBasePlaylists()).thenReturn(List.of());
		when(scheduleService.findAllSchedules()).thenReturn(List.of());
		when(playlistSelector.select(List.of(), List.of())).thenReturn(selection);
		when(sourceGenerator.generate(selection)).thenReturn("sources");
		when(scheduleGenerator.generate(selection)).thenReturn("schedule");
		doAnswer(invocation -> {
			restartStarted.countDown();
			releaseRestart.await(2, TimeUnit.SECONDS);
			return null;
		}).when(processControl).restartAndAwaitRecovery();

		Thread firstApplication = Thread.ofVirtual().start(() -> {
			try {
				liquidsoapService.applyConfiguration();
			} catch (Throwable throwable) {
				asynchronousFailure.set(throwable);
			}
		});
		assertThat(restartStarted.await(2, TimeUnit.SECONDS)).isTrue();

		assertThatThrownBy(() -> liquidsoapService.applyConfiguration())
			.isInstanceOf(LiquidsoapApplyInProgressException.class)
			.hasMessage("Liquidsoap configuration application is already in progress");

		releaseRestart.countDown();
		firstApplication.join();
		assertThat(asynchronousFailure.get()).isNull();
	}

	@Test
	void returnsDetailedPlaylistWithoutReadingWhenM3uIsNotAssociated() {
		Playlist playlist = new Playlist();
		playlist.setUuid("playlist-uuid");
		playlist.setName("Playlist");
		playlist.setWeight(1);
		when(playlistService.findPlaylist("playlist-uuid"))
			.thenReturn(playlist);

		PlaylistDetailsResponseDTO playlistDetails = liquidsoapService.showPlaylistDetails(
			"playlist-uuid"
		);

		assertThat(playlistDetails.tracks()).isEmpty();
		verify(m3uContentReader, never()).read(org.mockito.ArgumentMatchers.any());
	}

	@Test
	void returnsDetailedPlaylistWithTracksReadFromM3u() {
		Playlist playlist = new Playlist();
		playlist.setUuid("playlist-uuid");
		playlist.setName("Playlist");
		playlist.setM3uFileName("playlist-uuid.m3u");
		playlist.setWeight(1);
		when(playlistService.findPlaylist("playlist-uuid"))
			.thenReturn(playlist);
		when(m3uContentReader.read("playlist-uuid.m3u"))
			.thenReturn(List.of("rock/first.mp3"));

		PlaylistDetailsResponseDTO playlistDetails = liquidsoapService.showPlaylistDetails(
			"playlist-uuid"
		);

		assertThat(playlistDetails.tracks()).containsExactly("rock/first.mp3");
	}

	@Test
	void replacesTracksBeforeAssociatingAutomaticM3uName() {
		Playlist playlist = new Playlist();
		playlist.setUuid("playlist-uuid");
		playlist.setName("Playlist");
		playlist.setWeight(1);
		List<String> mediaPaths = List.of("rock/first.mp3", "rock/second.flac");
		when(playlistService.findPlaylist("playlist-uuid"))
			.thenReturn(playlist);
		when(m3uContentGenerator.fromRelativeMediaPaths(mediaPaths))
			.thenReturn("#EXTM3U\n/media/first.mp3\n/media/second.flac\n");
		when(m3uContentReader.read("playlist-uuid.m3u"))
			.thenReturn(mediaPaths);
		when(playlistService.associateM3uFileName("playlist-uuid", "playlist-uuid.m3u"))
			.thenAnswer(invocation -> {
				playlist.setM3uFileName(invocation.getArgument(1));
				return playlist;
			});

		PlaylistDetailsResponseDTO playlistDetails = liquidsoapService.setPlaylistTracks(
			"playlist-uuid",
			mediaPaths
		);

		assertThat(playlistDetails.m3uFileName()).isEqualTo("playlist-uuid.m3u");
		assertThat(playlistDetails.tracks()).containsExactlyElementsOf(mediaPaths);
		InOrder replacementOrder = inOrder(
			playlistService,
			m3uContentGenerator,
			fileWriter,
			m3uContentReader
		);
		replacementOrder.verify(playlistService).findPlaylist("playlist-uuid");
		replacementOrder.verify(m3uContentGenerator).fromRelativeMediaPaths(mediaPaths);
		replacementOrder.verify(fileWriter).writeM3u(
			"playlist-uuid.m3u",
			"#EXTM3U\n/media/first.mp3\n/media/second.flac\n"
		);
		replacementOrder.verify(m3uContentReader).read("playlist-uuid.m3u");
		replacementOrder.verify(playlistService)
			.associateM3uFileName("playlist-uuid", "playlist-uuid.m3u");
		verify(processControl, never()).restartAndAwaitRecovery();
	}

	@Test
	void preservesM3uAssociationWhenTrackValidationFails() {
		Playlist playlist = new Playlist();
		playlist.setUuid("playlist-uuid");
		playlist.setM3uFileName("current.m3u");
		when(playlistService.findPlaylist("playlist-uuid"))
			.thenReturn(playlist);
		when(m3uContentGenerator.fromRelativeMediaPaths(List.of()))
			.thenThrow(new LiquidsoapFileException("At least one media path is required"));

		assertThatThrownBy(() -> liquidsoapService.setPlaylistTracks("playlist-uuid", List.of()))
			.isInstanceOf(LiquidsoapFileException.class)
			.hasMessage("At least one media path is required");

		verify(fileWriter, never()).writeM3u(
			org.mockito.ArgumentMatchers.any(),
			org.mockito.ArgumentMatchers.any()
		);
		verify(playlistService, never()).associateM3uFileName(
			org.mockito.ArgumentMatchers.any(),
			org.mockito.ArgumentMatchers.any()
		);
	}

	@Test
	void doesNotGenerateM3uWhenPlaylistDoesNotExist() {
		when(playlistService.findPlaylist("unknown"))
			.thenThrow(new ResourceNotFoundException("Playlist", "unknown"));

		assertThatThrownBy(() -> liquidsoapService.setPlaylistTracks(
			"unknown",
			List.of("rock/first.mp3")
		))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("Playlist not found with uuid: unknown");

		verify(m3uContentGenerator, never())
			.fromRelativeMediaPaths(org.mockito.ArgumentMatchers.any());
		verify(fileWriter, never()).writeM3u(
			org.mockito.ArgumentMatchers.any(),
			org.mockito.ArgumentMatchers.any()
		);
	}
}
