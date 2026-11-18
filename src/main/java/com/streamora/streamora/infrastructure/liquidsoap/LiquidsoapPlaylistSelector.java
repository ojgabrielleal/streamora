package com.streamora.streamora.infrastructure.liquidsoap;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.Schedule;
import com.streamora.streamora.entity.type.WeekDay;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapScheduleSlot;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapSelection;
import com.streamora.streamora.infrastructure.liquidsoap.records.WeightedPlaylist;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LiquidsoapPlaylistSelector {

	private final PlaylistAvailabilityChecker availabilityChecker;

	public LiquidsoapSelection select(List<Playlist> basePlaylists, List<Schedule> schedules) {
		List<WeightedPlaylist> availableBasePlaylists = uniqueAvailable(basePlaylists);
		List<LiquidsoapScheduleSlot> scheduleSlots = createScheduleSlots(schedules);

		return new LiquidsoapSelection(availableBasePlaylists, scheduleSlots);
	}

	public List<WeightedPlaylist> uniqueAvailable(List<Playlist> playlists) {
		Map<String, WeightedPlaylist> uniquePlaylists = new LinkedHashMap<>();

		playlists.stream()
			.filter(Objects::nonNull)
			.filter(availabilityChecker::isAvailable)
			.filter(playlist -> playlist.getWeight() > 0)
			.map(this::toWeightedPlaylist)
			.forEach(playlist -> uniquePlaylists.putIfAbsent(playlist.uuid(), playlist));

		return List.copyOf(uniquePlaylists.values());
	}

	private List<LiquidsoapScheduleSlot> createScheduleSlots(List<Schedule> schedules) {
		List<LiquidsoapScheduleSlot> scheduleSlots = new ArrayList<>();
		int slotIndex = 1;

		for (WeekDay dayOfWeek : EnumSet.allOf(WeekDay.class)) {
			List<Schedule> daySchedules = schedules.stream()
				.filter(schedule -> schedule.getDayOfWeek() == dayOfWeek)
				.toList();
			List<LocalTime> boundaries = boundaries(daySchedules);

			for (int boundaryIndex = 0; boundaryIndex < boundaries.size() - 1; boundaryIndex++) {
				LocalTime startTime = boundaries.get(boundaryIndex);
				LocalTime endTime = boundaries.get(boundaryIndex + 1);
				List<WeightedPlaylist> activePlaylists = activePlaylists(daySchedules, startTime);

				if (!activePlaylists.isEmpty()) {
					scheduleSlots.add(new LiquidsoapScheduleSlot(
						"scheduled_" + slotIndex++,
						dayOfWeek,
						startTime,
						endTime,
						activePlaylists
					));
				}
			}
		}

		return mergeAdjacentSlots(scheduleSlots);
	}

	private List<LocalTime> boundaries(List<Schedule> schedules) {
		return schedules.stream()
			.flatMap(schedule -> List.of(schedule.getStartTime(), schedule.getEndTime()).stream())
			.distinct()
			.sorted()
			.toList();
	}

	private List<WeightedPlaylist> activePlaylists(List<Schedule> schedules, LocalTime time) {
		List<Playlist> activePlaylists = schedules.stream()
			.filter(schedule -> !time.isBefore(schedule.getStartTime()))
			.filter(schedule -> time.isBefore(schedule.getEndTime()))
			.map(schedule -> schedule.getPlaylist())
			.toList();

		return uniqueAvailable(activePlaylists);
	}

	private List<LiquidsoapScheduleSlot> mergeAdjacentSlots(List<LiquidsoapScheduleSlot> slots) {
		List<LiquidsoapScheduleSlot> mergedSlots = new ArrayList<>();

		for (LiquidsoapScheduleSlot slot : slots) {
			if (mergedSlots.isEmpty()) {
				mergedSlots.add(slot);
				continue;
			}

			LiquidsoapScheduleSlot previousSlot = mergedSlots.getLast();

			if (previousSlot.dayOfWeek() == slot.dayOfWeek()
				&& previousSlot.endTime().equals(slot.startTime())
				&& previousSlot.playlists().equals(slot.playlists())) {
				mergedSlots.set(
					mergedSlots.size() - 1,
					new LiquidsoapScheduleSlot(
						previousSlot.sourceName(),
						previousSlot.dayOfWeek(),
						previousSlot.startTime(),
						slot.endTime(),
						previousSlot.playlists()
					)
				);
				continue;
			}

			mergedSlots.add(slot);
		}

		return List.copyOf(mergedSlots);
	}

	private WeightedPlaylist toWeightedPlaylist(Playlist playlist) {
		return new WeightedPlaylist(
			playlist.getUuid(),
			"playlist_" + playlist.getUuid().replace('-', '_'),
			playlist.getM3uFileName(),
			playlist.getWeight()
		);
	}
}
