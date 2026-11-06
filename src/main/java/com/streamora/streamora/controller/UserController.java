package com.streamora.streamora.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.streamora.streamora.dto.UserRequestDTO;
import com.streamora.streamora.dto.UserResponseDTO;
import com.streamora.streamora.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	public List<UserResponseDTO> index() {
		return userService.index();
	}

	@GetMapping("/{uuid}")
	public UserResponseDTO show(@PathVariable String uuid) {
		return userService.show(uuid);
	}

	@PostMapping
	public UserResponseDTO store(@RequestBody UserRequestDTO request) {
		return userService.store(request);
	}

	@PutMapping("/{uuid}")
	public UserResponseDTO update(@PathVariable String uuid, @RequestBody UserRequestDTO request) {
		return userService.update(uuid, request);
	}

	@DeleteMapping("/{uuid}")
	public void delete(@PathVariable String uuid) {
		userService.delete(uuid);
	}
}
