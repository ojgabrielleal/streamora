package com.streamora.streamora.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.streamora.streamora.StreamoraApplication;
import com.streamora.streamora.repository.PlaylistRepository;
import com.streamora.streamora.repository.ScheduleRepository;
import com.streamora.streamora.repository.UserRepository;

@SpringBootTest(
	classes = StreamoraApplication.class,
	properties = {
		"spring.autoconfigure.exclude="
			+ "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
			+ "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration",
		"jwt.secret=context-test-secret-that-is-not-used-outside-tests"
	}
)
@AutoConfigureMockMvc
class OpenApiDocumentationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private PlaylistRepository playlistRepository;

	@MockitoBean
	private ScheduleRepository scheduleRepository;

	@Test
	void exposesOpenApiMetadataAndDocumentedResourcesAnonymously() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.info.title").value("Streamora API"))
			.andExpect(jsonPath("$.info.description").value("API para gerenciamento de web-rádio Streamora."))
			.andExpect(jsonPath("$.info.version").value("1.0.0"))
			.andExpect(jsonPath("$.paths['/login'].post.tags[0]").value("Autenticação"))
			.andExpect(jsonPath("$.paths['/login'].post.requestBody.required").value(true))
			.andExpect(jsonPath("$.paths['/login'].post.requestBody.content['application/json'].schema['$ref']")
				.value("#/components/schemas/AuthRequestDTO"))
			.andExpect(jsonPath("$.paths['/login'].post.responses['200']").exists())
			.andExpect(jsonPath("$.paths['/login'].post.responses['401']").exists())
			.andExpect(jsonPath("$.paths['/user'].get.tags[0]").value("Usuários"))
			.andExpect(jsonPath("$.paths['/user'].post.requestBody.required").value(true))
			.andExpect(jsonPath("$.paths['/user/{uuid}'].get.parameters[0].name").value("uuid"))
			.andExpect(jsonPath("$.paths['/user/{uuid}'].put").exists())
			.andExpect(jsonPath("$.paths['/user/{uuid}'].delete").exists())
			.andExpect(jsonPath("$.paths['/playlist'].get.tags[0]").value("Playlists"))
			.andExpect(jsonPath("$.paths['/playlist'].post.requestBody.required").value(true))
			.andExpect(jsonPath("$.paths['/playlist/{uuid}'].get").exists())
			.andExpect(jsonPath("$.paths['/playlist/{uuid}'].put.parameters[0].name").value("uuid"))
			.andExpect(jsonPath("$.paths['/playlist/{uuid}'].delete").exists())
			.andExpect(jsonPath("$.paths['/schedules'].get.tags[0]").value("Grade de programação"))
			.andExpect(jsonPath("$.paths['/schedules'].post.requestBody.required").value(true))
			.andExpect(jsonPath("$.paths['/schedules/{uuid}'].get").exists())
			.andExpect(jsonPath("$.paths['/schedules/{uuid}'].put").exists())
			.andExpect(jsonPath("$.paths['/schedules/{uuid}'].delete").exists())
			.andExpect(jsonPath("$.paths['/storage'].get.tags[0]").value("Storage FTP"))
			.andExpect(jsonPath("$.paths['/storage'].get.responses['200']").exists())
			.andExpect(jsonPath("$.paths['/storage'].get.responses['400']").exists())
			.andExpect(jsonPath("$.paths['/storage'].get.responses['401']").exists())
			.andExpect(jsonPath("$.paths['/storage/files'].delete.parameters[0].name").value("path"))
			.andExpect(jsonPath("$.paths['/storage/files'].delete.responses['204']").exists())
			.andExpect(jsonPath("$.paths['/storage/files/rename'].put.requestBody.required").value(true))
			.andExpect(jsonPath("$.paths['/storage/files/rename'].put.responses['204']").exists())
			.andExpect(jsonPath("$.paths['/storage/files/move'].put.requestBody.required").value(true))
			.andExpect(jsonPath("$.paths['/storage/directories'].post.requestBody.required").value(true))
			.andExpect(jsonPath("$.paths['/storage/directories'].delete.parameters[0].name").value("path"))
			.andExpect(jsonPath("$.paths['/storage/directories/rename'].put.requestBody.required").value(true));
	}

	@Test
	void documentsAllRequestAndResponseSchemas() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.components.schemas.AuthRequestDTO").exists())
			.andExpect(jsonPath("$.components.schemas.AuthRequestDTO.required").value(org.hamcrest.Matchers.containsInAnyOrder("username", "password")))
			.andExpect(jsonPath("$.components.schemas.AuthRequestDTO.properties.password.writeOnly").value(true))
			.andExpect(jsonPath("$.components.schemas.UserRequestDTO").exists())
			.andExpect(jsonPath("$.components.schemas.UserRequestDTO.properties.password.writeOnly").value(true))
			.andExpect(jsonPath("$.components.schemas.UserResponseDTO").exists())
			.andExpect(jsonPath("$.components.schemas.PlaylistRequestDTO").exists())
			.andExpect(jsonPath("$.components.schemas.PlaylistRequestDTO.properties.m3uFileName").exists())
			.andExpect(jsonPath("$.components.schemas.PlaylistResponseDTO").exists())
			.andExpect(jsonPath("$.components.schemas.PlaylistResponseDTO.properties.m3uFileName").exists())
			.andExpect(jsonPath("$.components.schemas.ScheduleRequestDTO").exists())
			.andExpect(jsonPath("$.components.schemas.ScheduleResponseDTO").exists())
			.andExpect(jsonPath("$.components.schemas.FtpDirectoryCreateRequestDTO.required[0]").value("path"))
			.andExpect(jsonPath("$.components.schemas.FtpRenameRequestDTO.required").value(org.hamcrest.Matchers.containsInAnyOrder("path", "newName")))
			.andExpect(jsonPath("$.components.schemas.FtpMoveRequestDTO.required").value(org.hamcrest.Matchers.containsInAnyOrder("path", "targetDirectory")))
			.andExpect(jsonPath("$.components.schemas.FtpStorageErrorDTO.properties.message").exists())
			.andExpect(jsonPath("$.components.schemas.FtpFileEntryResponseDTO.properties.name").exists())
			.andExpect(jsonPath("$.components.schemas.FtpFileEntryResponseDTO.properties.type").exists())
			.andExpect(jsonPath("$.components.schemas.FtpFileEntryResponseDTO.properties.children.items['$ref']")
				.value("#/components/schemas/FtpFileEntryResponseDTO"))
			.andExpect(jsonPath("$.components.schemas.FtpFileEntry").doesNotExist());
	}

	@Test
	void exposesSwaggerUiAnonymously() throws Exception {
		mockMvc.perform(get("/swagger-ui.html"))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/swagger-ui/index.html"));

		mockMvc.perform(get("/swagger-ui/index.html"))
			.andExpect(status().isOk());
	}

	@Test
	void protectsNonGetDocumentationRequestsAndApplicationEndpoints() throws Exception {
		mockMvc.perform(post("/v3/api-docs"))
			.andExpect(status().isUnauthorized());

		mockMvc.perform(get("/user"))
			.andExpect(status().isUnauthorized());
	}
}
