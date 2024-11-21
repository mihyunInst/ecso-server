package com.ecso.project.auth.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecso.project.auth.model.dto.Token;
import com.ecso.project.common.util.JwtUtil;
import com.ecso.project.user.model.dto.User;
import com.ecso.project.user.model.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService{

	private final JwtUtil jwtUtil;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	//private final UserRepository repository;
	private final UserMapper userMapper;
	
	@Override
	public Map<String, Object> login(User user) {
		
		// DB에서 해당 이메일 가진 회원 조회
		User loginUser = userMapper.login(user.getUserEmail());
		
		// 비밀번호 검증
		if(!bCryptPasswordEncoder.matches(user.getUserPw(), loginUser.getUserPw())) {
			 throw new RuntimeException("Invalid password");
		}
		
		// 토큰발급
		String accessToken = jwtUtil.generateAccessToken(loginUser.getUserEmail(), loginUser.getUserRole());
        String refreshToken = jwtUtil.generateRefreshToken(loginUser.getUserEmail());
        
        loginUser.setUserPw(null);
        
        Map<String, Object> map = new HashMap<>();
        map.put("user", loginUser);
        map.put("token", Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
        
        // 유저정보 & 토큰 반환
        return map;
    
	}
}
