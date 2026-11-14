package com.streamora.streamora.infrastructure.liquidsoap;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.streamora.streamora.entity.type.WeekDay;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapScheduleSlot;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapSelection;

@Component
public class LiquidsoapScheduleGenerator {

	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH'h'mm");

	public String generate(LiquidsoapSelection selection) {
		StringBuilder content = new StringBuilder();
		Map<WeekDay, List<LiquidsoapScheduleSlot>> slotsByDay = slotsByDay(selection.scheduleSlots());

		for (Map.Entry<WeekDay, List<LiquidsoapScheduleSlot>> daySlots : slotsByDay.entrySet()) {
			appendDaySchedule(content, daySlots.getKey(), daySlots.getValue());
			content.append('\n');
		}

		List<String> radioSources = new ArrayList<>();

		if (!slotsByDay.isEmpty()) {
			appendWeeklySchedule(content, slotsByDay.keySet().stream().toList());
			content.append('\n');
			radioSources.add("scheduled_radio");
		}

		if (!selection.basePlaylists().isEmpty()) {
			radioSources.add("base_radio");
		}

		radioSources.add("blank()");
		content.append("radio = fallback(track_sensitive=false, [")
			.append(String.join(", ", radioSources))
			.append("])\n");

		return content.toString();
	}

	private Map<WeekDay, List<LiquidsoapScheduleSlot>> slotsByDay(
		List<LiquidsoapScheduleSlot> scheduleSlots
	)
	{
		Map<WeekDay, List<LiquidsoapScheduleSlot>> slotsByDay = new EnumMap<>(WeekDay.class);

		for (LiquidsoapScheduleSlot scheduleSlot : scheduleSlots) {
			slotsByDay.computeIfAbsent(scheduleSlot.dayOfWeek(), day -> new ArrayList<>())
				.add(scheduleSlot);
		}

		return slotsByDay;
	}

	private void appendDaySchedule(
		StringBuilder content,
		WeekDay dayOfWeek,
		List<LiquidsoapScheduleSlot> scheduleSlots
	)
	{
		String branches = scheduleSlots.stream()
			.map(slot -> "({" + time(slot.startTime()) + "-" + time(slot.endTime()) + "}, "
				+ slot.sourceName() + ")")
			.collect(Collectors.joining(", "));

		content.append(daySourceName(dayOfWeek))
			.append(" = switch(track_sensitive=false, [")
			.append(branches)
			.append("])\n");
	}

	private void appendWeeklySchedule(StringBuilder content, List<WeekDay> scheduledDays) {
		String branches = scheduledDays.stream()
			.map(day -> "({" + liquidsoapDay(day) + "w}, " + daySourceName(day) + ")")
			.collect(Collectors.joining(", "));

		content.append("scheduled_radio = switch(track_sensitive=false, [")
			.append(branches)
			.append("])\n");
	}

	private String time(LocalTime time) {
		return TIME_FORMAT.format(time);
	}

	private String daySourceName(WeekDay dayOfWeek) {
		return dayOfWeek.name().toLowerCase() + "_schedule";
	}

	private int liquidsoapDay(WeekDay dayOfWeek) {
		return dayOfWeek.ordinal() + 1;
	}
}
