package com.ecso.project.auth.model.service;

import java.util.Map;

import com.ecso.project.user.model.dto.User;

public interface AuthService {

	Map<String, Object> login(User user);

	String sendAuthKey(String email);

	int checkAuthKey(String verificationCode, String token);
}
