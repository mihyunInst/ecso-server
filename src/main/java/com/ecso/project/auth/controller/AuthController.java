package com.ecso.project.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecso.project.auth.model.dto.Token;
import com.ecso.project.auth.model.service.AuthService;
import com.ecso.project.user.model.dto.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
@Slf4j
public class AuthController {

	private final AuthService authService;

	@PostMapping("login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
		Map<String, Object> map = authService.login(user);
		return ResponseEntity.ok(map);
	}
}
