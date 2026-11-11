package com.streamora.streamora.dto;

import java.time.LocalTime;

import com.streamora.streamora.entity.type.WeekDay;

public record PlaylistScheduleSummaryDTO(
	String uuid,
	WeekDay dayOfWeek,
	LocalTime startTime,
	LocalTime endTime
) {}
