package com.streamora.streamora.dto;

import java.time.LocalTime;

import com.streamora.streamora.entity.type.WeekDay;

public record ScheduleResponseDTO(
	String uuid,
	PlaylistSummaryDTO playlist,
	WeekDay dayOfWeek,
	LocalTime startTime,
	LocalTime endTime
) {}
