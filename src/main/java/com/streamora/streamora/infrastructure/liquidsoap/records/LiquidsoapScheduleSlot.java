package com.streamora.streamora.infrastructure.liquidsoap.records;

import java.time.LocalTime;
import java.util.List;

import com.streamora.streamora.entity.type.WeekDay;

public record LiquidsoapScheduleSlot(
	String sourceName,
	WeekDay dayOfWeek,
	LocalTime startTime,
	LocalTime endTime,
	List<WeightedPlaylist> playlists
) {}
