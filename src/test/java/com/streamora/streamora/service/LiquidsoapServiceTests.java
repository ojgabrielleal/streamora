package com.streamora.streamora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.infrastructure.liquidsoap.LiquidsoapFileWriter;
import com.streamora.streamora.infrastructure.liquidsoap.LiquidsoapPlaylistSelector;
import com.streamora.streamora.infrastructure.liquidsoap.LiquidsoapScheduleGenerator;
import com.streamora.streamora.infrastructure.liquidsoap.LiquidsoapSourceGenerator;
import com.streamora.streamora.infrastructure.liquidsoap.M3uContentGenerator;
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
	private LiquidsoapFileWriter fileWriter;

	@Mock
	private LiquidsoapPlaylistSelector playlistSelector;

	@Mock
	private LiquidsoapSourceGenerator sourceGenerator;

	@Mock
	private LiquidsoapScheduleGenerator scheduleGenerator;

	private LiquidsoapService liquidsoapService;

	@BeforeEach
	void setUp() {
		liquidsoapService = new LiquidsoapService(
			playlistService,
			scheduleService,
			m3uContentGenerator,
			fileWriter,
			playlistSelector,
			sourceGenerator,
			scheduleGenerator
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
}
