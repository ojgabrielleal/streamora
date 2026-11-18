package com.streamora.streamora.infrastructure.liquidsoap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapScheduleSlot;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapSelection;
import com.streamora.streamora.infrastructure.liquidsoap.records.WeightedPlaylist;

@Component
public class LiquidsoapSourceGenerator {

	private static final String M3U_DIRECTORY = "/liquidsoap/m3u/";

	public String generate(LiquidsoapSelection selection) {
		StringBuilder content = new StringBuilder();

		allPlaylists(selection).values().forEach(playlist -> content
			.append(playlist.sourceName())
			.append(" = playlist(mode=\"randomize\", reload=60, reload_mode=\"seconds\", \"")
			.append(M3U_DIRECTORY)
			.append(playlist.m3uFileName())
			.append("\")\n")
		);

		if (!selection.basePlaylists().isEmpty()) {
			content.append('\n');
			appendCombination(content, "base_radio", selection.basePlaylists());
		}

		for (LiquidsoapScheduleSlot scheduleSlot : selection.scheduleSlots()) {
			content.append('\n');
			appendCombination(content, scheduleSlot.sourceName(), scheduleSlot.playlists());
		}

		return content.toString();
	}

	private Map<String, WeightedPlaylist> allPlaylists(LiquidsoapSelection selection) {
		Map<String, WeightedPlaylist> playlists = new LinkedHashMap<>();
		selection.basePlaylists().forEach(playlist -> playlists.putIfAbsent(playlist.uuid(), playlist));
		selection.scheduleSlots().stream()
			.flatMap(scheduleSlot -> scheduleSlot.playlists().stream())
			.forEach(playlist -> playlists.putIfAbsent(playlist.uuid(), playlist));

		return playlists;
	}

	private void appendCombination(
		StringBuilder content,
		String sourceName,
		List<WeightedPlaylist> playlists
	)
	{
		if (playlists.size() == 1) {
			content.append(sourceName)
				.append(" = ")
				.append(playlists.getFirst().sourceName())
				.append('\n');
			return;
		}

		String weights = playlists.stream()
			.map(playlist -> Integer.toString(playlist.weight()))
			.collect(Collectors.joining(", "));
		String sources = playlists.stream()
			.map(playlist -> playlist.sourceName())
			.collect(Collectors.joining(", "));

		content.append(sourceName)
			.append(" = rotate(weights=[")
			.append(weights)
			.append("], [")
			.append(sources)
			.append("])\n");
	}
}
