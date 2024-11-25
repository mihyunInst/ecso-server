package com.ecso.project.auth.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecso.project.common.util.JwtUtil;
import com.ecso.project.email.model.mapper.EmailMapper;
import com.ecso.project.email.model.service.EmailService;
import com.ecso.project.user.model.dto.User;
import com.ecso.project.user.model.mapper.UserMapper;

import io.jsonwebtoken.Claims;
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
	private final EmailService emailService;
	private final EmailMapper emailMapper;
	
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
        map.put("loginUser", loginUser);
        map.put("accessToken", accessToken);
        map.put("refreshToken", refreshToken);
        
        // 유저정보 & 토큰 반환
        return map;
    
	}
	
	
	@Override
	public String sendAuthKey(String email) {
		
		// DB에서 해당 이메일 가진 회원이 있는지 검사(이메일 중복검사)
		int result = userMapper.checkDupEmail(email);
		
		if(result > 0) return null; // 중복이면 리턴

		// 중복 아닐때
		String emailVerificationToken = null;
		
		// 인증번호 발급 및 DB저장,이메일전송
		String authKey = emailService.sendEmail("signup", email);
		
		if(authKey != null) { // 인증번호가 반환되서 돌아옴
								// == 이메일 보내기 성공
			// 임시토큰 발급하여 리턴
			emailVerificationToken = jwtUtil.generateVerificationToken(email);
			//log.debug("emailVerificationToken : " + emailVerificationToken);
			
		} 
		
		return emailVerificationToken;
	}
	
	@Override
	public int checkAuthKey(String verificationCode, String token) {
		
		if(!jwtUtil.validateToken(token)) {
			return 0;			
		}
		
		String email = jwtUtil.getUserEmailFromToken(token); // 토큰에서 이메일 꺼내오기
		
		Map<String , Object> map = new HashMap<>();
		map.put("email", email);
		map.put("authKey", verificationCode);
		
		int result = emailMapper.checkAuthKey(map);
		
		return result;
	}
}
