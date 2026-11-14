package com.streamora.streamora.infrastructure.liquidsoap;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.streamora.streamora.entity.type.WeekDay;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapScheduleSlot;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapSelection;
import com.streamora.streamora.infrastructure.liquidsoap.records.WeightedPlaylist;

class LiquidsoapConfigurationGeneratorTests {

	private final LiquidsoapSourceGenerator sourceGenerator = new LiquidsoapSourceGenerator();
	private final LiquidsoapScheduleGenerator scheduleGenerator = new LiquidsoapScheduleGenerator();

	@Test
	void generatesWeightedSourcesAndWeeklySchedule() {
		WeightedPlaylist base = playlist("base", 1);
		WeightedPlaylist first = playlist("first", 2);
		WeightedPlaylist second = playlist("second", 3);
		LiquidsoapScheduleSlot slot = new LiquidsoapScheduleSlot(
			"scheduled_1",
			WeekDay.MONDAY,
			LocalTime.of(8, 0),
			LocalTime.of(10, 30),
			List.of(first, second)
		);
		LiquidsoapSelection selection = new LiquidsoapSelection(List.of(base), List.of(slot));

		String sources = sourceGenerator.generate(selection);
		String schedule = scheduleGenerator.generate(selection);

		assertThat(sources)
			.contains("reload=60")
			.contains("scheduled_1 = rotate(weights=[2, 3], [playlist_first, playlist_second])")
			.doesNotContain("password");
		assertThat(schedule)
			.contains("({08h00-10h30}, scheduled_1)")
			.contains("({1w}, monday_schedule)")
			.contains("[scheduled_radio, base_radio, blank()]");
	}

	@Test
	void fallsBackDirectlyToBaseWithoutSchedules() {
		LiquidsoapSelection selection = new LiquidsoapSelection(
			List.of(playlist("base", 1)),
			List.of()
		);

		String schedule = scheduleGenerator.generate(selection);

		assertThat(schedule)
			.contains("radio = fallback(track_sensitive=false, [base_radio, blank()])")
			.doesNotContain("scheduled_radio");
	}

	private WeightedPlaylist playlist(String uuid, int weight) {
		return new WeightedPlaylist(uuid, "playlist_" + uuid, uuid + ".m3u", weight);
	}
}
