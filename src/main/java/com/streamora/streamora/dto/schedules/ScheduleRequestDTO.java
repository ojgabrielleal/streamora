package com.streamora.streamora.dto.schedules;

import java.time.LocalTime;

import com.streamora.streamora.entity.type.WeekDay;

import io.swagger.v3.oas.annotations.media.Schema;

public record ScheduleRequestDTO(
	@Schema(example = "550e8400-e29b-41d4-a716-446655440000")
	String playlistUuid,
	@Schema(example = "MONDAY")
	WeekDay dayOfWeek,
	@Schema(type = "string", format = "time", example = "08:00:00")
	LocalTime startTime,
	@Schema(type = "string", format = "time", example = "12:00:00")
	LocalTime endTime
) {}
