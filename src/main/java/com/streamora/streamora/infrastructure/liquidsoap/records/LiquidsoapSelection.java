package com.streamora.streamora.infrastructure.liquidsoap.records;

import java.util.List;

public record LiquidsoapSelection(
	List<WeightedPlaylist> basePlaylists,
	List<LiquidsoapScheduleSlot> scheduleSlots
) {}
