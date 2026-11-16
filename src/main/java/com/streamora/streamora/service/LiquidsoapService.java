package com.streamora.streamora.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Service;

import com.streamora.streamora.dto.playlists.PlaylistDetailsResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.infrastructure.liquidsoap.LiquidsoapFileWriter;
import com.streamora.streamora.infrastructure.liquidsoap.LiquidsoapPlaylistSelector;
import com.streamora.streamora.infrastructure.liquidsoap.LiquidsoapScheduleGenerator;
import com.streamora.streamora.infrastructure.liquidsoap.LiquidsoapSourceGenerator;
import com.streamora.streamora.infrastructure.liquidsoap.M3uContentGenerator;
import com.streamora.streamora.infrastructure.liquidsoap.M3uContentReader;
import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapApplyInProgressException;
import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapFileException;
import com.streamora.streamora.infrastructure.liquidsoap.interfaces.LiquidsoapProcessControl;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapSelection;
import com.streamora.streamora.mapper.PlaylistMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LiquidsoapService {

	public static final String SOURCES_FILE_NAME = "sources.liq";
	public static final String SCHEDULE_FILE_NAME = "schedule.liq";

	private final PlaylistService playlistService;
	private final ScheduleService scheduleService;
	private final M3uContentGenerator m3uContentGenerator;
	private final M3uContentReader m3uContentReader;
	private final LiquidsoapFileWriter fileWriter;
	private final LiquidsoapPlaylistSelector playlistSelector;
	private final LiquidsoapSourceGenerator sourceGenerator;
	private final LiquidsoapScheduleGenerator scheduleGenerator;
	private final LiquidsoapProcessControl processControl;
	private final AtomicBoolean applyingConfiguration = new AtomicBoolean();

	public Playlist generateM3uFromMedia(
		String playlistUuid,
		String m3uFileName,
		String mediaPath
	)
	{
		String content = m3uContentGenerator.fromMedia(mediaPath);

		return writeAndAssociate(playlistUuid, m3uFileName, content);
	}

	public Playlist generateM3uFromMediaPaths(
		String playlistUuid,
		String m3uFileName,
		List<String> mediaPaths
	)
	{
		String content = m3uContentGenerator.fromMediaPaths(mediaPaths);

		return writeAndAssociate(playlistUuid, m3uFileName, content);
	}

	public Playlist generateM3uFromDirectory(
		String playlistUuid,
		String m3uFileName,
		String directoryPath
	)
	{
		String content = m3uContentGenerator.fromDirectory(directoryPath);

		return writeAndAssociate(playlistUuid, m3uFileName, content);
	}

	public Playlist regenerateM3uFromMediaPaths(String playlistUuid, List<String> mediaPaths) {
		Playlist playlist = playlistService.findPlaylist(playlistUuid);
		String m3uFileName = playlist.getM3uFileName();

		if (m3uFileName == null) {
			throw new LiquidsoapFileException("Playlist does not have an associated M3U file");
		}

		String content = m3uContentGenerator.fromMediaPaths(mediaPaths);
		fileWriter.writeM3u(m3uFileName, content);

		return playlist;
	}

	public PlaylistDetailsResponseDTO showPlaylistDetails(String playlistUuid) {
		Playlist playlist = playlistService.findPlaylist(playlistUuid);
		List<String> tracks = playlist.getM3uFileName() == null
			? List.of()
			: m3uContentReader.read(playlist.getM3uFileName());

		return PlaylistMapper.toDetails(playlist, tracks);
	}

	public PlaylistDetailsResponseDTO setPlaylistTracks(
		String playlistUuid,
		List<String> mediaPaths
	)
	{
		Playlist playlist = playlistService.findPlaylist(playlistUuid);
		String m3uFileName = playlist.getUuid() + ".m3u";
		String content = m3uContentGenerator.fromRelativeMediaPaths(mediaPaths);

		fileWriter.writeM3u(m3uFileName, content);
		List<String> tracks = m3uContentReader.read(m3uFileName);
		Playlist updatedPlaylist = playlistService.associateM3uFileName(
			playlistUuid,
			m3uFileName
		);

		return PlaylistMapper.toDetails(updatedPlaylist, tracks);
	}

	public void generateConfiguration() {
		LiquidsoapSelection selection = playlistSelector.select(
			playlistService.findBasePlaylists(),
			scheduleService.findAllSchedules()
		);
		String sourcesContent = sourceGenerator.generate(selection);
		String scheduleContent = scheduleGenerator.generate(selection);

		fileWriter.writeConfiguration(SOURCES_FILE_NAME, sourcesContent);
		fileWriter.writeConfiguration(SCHEDULE_FILE_NAME, scheduleContent);
	}

	public void applyConfiguration() {
		if (!applyingConfiguration.compareAndSet(false, true)) {
			throw new LiquidsoapApplyInProgressException();
		}

		try {
			generateConfiguration();
			processControl.restartAndAwaitRecovery();
		} finally {
			applyingConfiguration.set(false);
		}
	}

	private Playlist writeAndAssociate(
		String playlistUuid,
		String m3uFileName,
		String content
	)
	{
		fileWriter.writeM3u(m3uFileName, content);

		return playlistService.associateM3uFileName(playlistUuid, m3uFileName);
	}
}
