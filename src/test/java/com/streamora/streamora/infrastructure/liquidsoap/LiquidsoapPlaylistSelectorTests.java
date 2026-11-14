package com.streamora.streamora.infrastructure.liquidsoap;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.Schedule;
import com.streamora.streamora.entity.type.WeekDay;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapProperties;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapSelection;

class LiquidsoapPlaylistSelectorTests {

	@TempDir
	Path temporaryDirectory;

	private Path m3uDirectory;
	private LiquidsoapPlaylistSelector selector;

	@BeforeEach
	void setUp() throws Exception {
		m3uDirectory = Files.createDirectory(temporaryDirectory.resolve("m3u"));
		LiquidsoapProperties properties = new LiquidsoapProperties(
			temporaryDirectory.resolve("media").toString(),
			m3uDirectory.toString(),
			temporaryDirectory.resolve("config").toString()
		);
		selector = new LiquidsoapPlaylistSelector(new PlaylistAvailabilityChecker(properties));
	}

	@Test
	void combinesSimultaneousSchedulesByWeightAndRemovesOverlappingDuplicate() throws Exception {
		Playlist firstPlaylist = availablePlaylist("first", 2);
		Playlist secondPlaylist = availablePlaylist("second", 3);
		List<Schedule> schedules = List.of(
			schedule(firstPlaylist, LocalTime.of(8, 0), LocalTime.of(11, 0)),
			schedule(firstPlaylist, LocalTime.of(9, 0), LocalTime.of(10, 0)),
			schedule(secondPlaylist, LocalTime.of(9, 0), LocalTime.of(12, 0))
		);

		LiquidsoapSelection selection = selector.select(List.of(), schedules);

		assertThat(selection.scheduleSlots())
			.anySatisfy(slot -> {
				assertThat(slot.startTime()).isEqualTo(LocalTime.of(9, 0));
				assertThat(slot.endTime()).isEqualTo(LocalTime.of(11, 0));
				assertThat(slot.playlists())
					.extracting("uuid", "weight")
					.containsExactlyInAnyOrder(
						tuple("first", 2),
						tuple("second", 3)
					);
			});
	}

	@Test
	void ignoresUnavailablePlaylistAndKeepsValidBaseFallback() throws Exception {
		Playlist validBase = availablePlaylist("base", 1);
		validBase.setBase(true);
		Playlist unavailableScheduled = playlist("missing", 4);
		Schedule schedule = schedule(
			unavailableScheduled,
			LocalTime.of(8, 0),
			LocalTime.of(10, 0)
		);

		LiquidsoapSelection selection = selector.select(List.of(validBase), List.of(schedule));

		assertThat(selection.basePlaylists()).extracting("uuid").containsExactly("base");
		assertThat(selection.scheduleSlots()).isEmpty();
	}

	@Test
	void ignoresEmptyM3uIndividually() throws Exception {
		Playlist emptyPlaylist = playlist("empty", 1);
		Playlist validPlaylist = availablePlaylist("valid", 2);
		Files.writeString(m3uDirectory.resolve(emptyPlaylist.getM3uFileName()), "#EXTM3U\n");

		assertThat(selector.uniqueAvailable(List.of(emptyPlaylist, validPlaylist)))
			.extracting("uuid")
			.containsExactly("valid");
	}

	private Playlist availablePlaylist(String uuid, int weight) throws Exception {
		Playlist playlist = playlist(uuid, weight);
		Files.writeString(m3uDirectory.resolve(playlist.getM3uFileName()), "#EXTM3U\n/media.mp3\n");

		return playlist;
	}

	private Playlist playlist(String uuid, int weight) {
		Playlist playlist = new Playlist();
		playlist.setUuid(uuid);
		playlist.setName(uuid);
		playlist.setM3uFileName(uuid + ".m3u");
		playlist.setWeight(weight);

		return playlist;
	}

	private Schedule schedule(Playlist playlist, LocalTime startTime, LocalTime endTime) {
		Schedule schedule = new Schedule();
		schedule.setPlaylist(playlist);
		schedule.setDayOfWeek(WeekDay.MONDAY);
		schedule.setStartTime(startTime);
		schedule.setEndTime(endTime);

		return schedule;
	}

	private static org.assertj.core.groups.Tuple tuple(Object... values) {
		return org.assertj.core.groups.Tuple.tuple(values);
	}
}
