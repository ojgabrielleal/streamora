package com.streamora.streamora.dto.schedules;

import java.time.LocalTime;

import com.streamora.streamora.entity.type.WeekDay;

public record ScheduleSummaryDTO(
	String uuid,
	WeekDay dayOfWeek,
	LocalTime startTime,
	LocalTime endTime
) {}
