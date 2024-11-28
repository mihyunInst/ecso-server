package com.ecso.project.auth.model.service;

import java.util.Map;

import com.ecso.project.user.model.dto.User;

public interface AuthService {
	
	void saveRefreshToken(int userNo, String refreshToken);

	Map<String, Object> login(User user);

	void removeRefreshToken(String token);

	void removeRefreshTokenByUserNo(int userNo);

	User validateRefreshToken(String refreshToken);
}
