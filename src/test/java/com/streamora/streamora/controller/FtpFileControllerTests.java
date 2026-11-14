package com.streamora.streamora.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.streamora.streamora.config.SecurityConfig;
import com.streamora.streamora.filter.JwtAuthenticationFilter;
import com.streamora.streamora.infrastructure.pureFtpd.storage.exception.FtpFileStorageException;
import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileEntry;
import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileType;
import com.streamora.streamora.service.FtpFileService;
import com.streamora.streamora.service.JwtService;

@WebMvcTest(FtpFileController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class FtpFileControllerTests {

	private static final String ACCESS_TOKEN = "valid-access-token";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private FtpFileService ftpFileService;

	@MockitoBean
	private JwtService jwtService;

	@BeforeEach
	void acceptAccessToken() {
		when(jwtService.validateAccessToken(ACCESS_TOKEN))
			.thenReturn(true);
	}

	@Test
	void listsStorageTree() throws Exception {
		when(ftpFileService.list())
			.thenReturn(List.of(
				new FtpFileEntry(
					"programas",
					FtpFileType.DIRECTORY,
					List.of(new FtpFileEntry("abertura.mp3", FtpFileType.FILE, List.of()))
				)
			));

		mockMvc.perform(
			get("/storage")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
		)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].name").value("programas"))
		.andExpect(jsonPath("$[0].type").value("DIRECTORY"))
		.andExpect(jsonPath("$[0].children[0].name").value("abertura.mp3"));

		verify(ftpFileService).list();
	}

	@Test
	void deletesFile() throws Exception {
		mockMvc.perform(
			delete("/storage/files")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
				.queryParam("path", "programas/abertura.mp3")
		)
		.andExpect(status().isNoContent())
		.andExpect(content().string(""));

		verify(ftpFileService).deleteFile("programas/abertura.mp3");
	}

	@Test
	void renamesFile() throws Exception {
		mockMvc.perform(
			put("/storage/files/rename")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"path":"programas/abertura.mp3","newName":"nova-abertura.mp3"}
					""")
		)
		.andExpect(status().isNoContent())
		.andExpect(content().string(""));

		verify(ftpFileService).renameFile("programas/abertura.mp3", "nova-abertura.mp3");
	}

	@Test
	void movesFileToRoot() throws Exception {
		mockMvc.perform(
			put("/storage/files/move")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"path":"programas/abertura.mp3","targetDirectory":""}
					""")
		)
		.andExpect(status().isNoContent())
		.andExpect(content().string(""));

		verify(ftpFileService).moveFile("programas/abertura.mp3", "");
	}

	@Test
	void createsDirectory() throws Exception {
		mockMvc.perform(
			post("/storage/directories")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"path":"programas/jornal"}
					""")
		)
		.andExpect(status().isNoContent())
		.andExpect(content().string(""));

		verify(ftpFileService).createDirectory("programas/jornal");
	}

	@Test
	void renamesDirectory() throws Exception {
		mockMvc.perform(
			put("/storage/directories/rename")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"path":"programas/jornal","newName":"noticias"}
					""")
		)
		.andExpect(status().isNoContent())
		.andExpect(content().string(""));

		verify(ftpFileService).renameDirectory("programas/jornal", "noticias");
	}

	@Test
	void deletesEmptyDirectory() throws Exception {
		mockMvc.perform(
			delete("/storage/directories")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
				.queryParam("path", "programas/antigos")
		)
		.andExpect(status().isNoContent())
		.andExpect(content().string(""));

		verify(ftpFileService).deleteDirectory("programas/antigos");
	}

	@Test
	void returnsSafeBadRequestWhenStorageOperationFails() throws Exception {
		doThrow(new FtpFileStorageException("FTP destination already exists"))
			.when(ftpFileService)
			.deleteFile("programas/abertura.mp3");

		mockMvc.perform(
			delete("/storage/files")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
				.queryParam("path", "programas/abertura.mp3")
		)
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("FTP destination already exists"))
		.andExpect(jsonPath("$.cause").doesNotExist())
		.andExpect(jsonPath("$.stackTrace").doesNotExist());
	}
}
