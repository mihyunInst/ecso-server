package com.ecso.project.auth.model.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecso.project.auth.model.dao.RefreshTokenRepository;
import com.ecso.project.auth.model.dto.RefreshToken;
import com.ecso.project.common.util.JwtUtil;
import com.ecso.project.user.model.dto.User;
import com.ecso.project.user.model.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final JwtUtil jwtUtil;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserMapper userMapper;
	private final RefreshTokenRepository refreshTokenRepository;
	
	// 리프레시 토큰 검증 (DB에 있는 회원 찾기)
	@Override
	public User validateRefreshToken(String refreshToken) {
		// 1. refresh 토큰 유효성 검증
        boolean isValid = jwtUtil.validateToken(refreshToken);

        User user = null;
        if(isValid) { // 유효하다면

            // 2. 토큰에서 이메일 추출
        	String email = jwtUtil.getUserEmailFromToken(refreshToken);

            // 3. 해당 이메일로 멤버 찾기
        	user = userMapper.getUserByEmail(email);
        	
        } 
        return user;
	}

	// 리프레시 토큰 DB 저장
	@Override
	public void saveRefreshToken(int userNo, String refreshToken) {
		RefreshToken token = 
				RefreshToken.builder().
				userNo(userNo).
				refreshToken(refreshToken)
				.expiresAt(LocalDateTime.now().plusDays(14)) // 14일 후 만료
				//.expiresAt(LocalDateTime.now().plusSeconds(30)) // 14일 후 만료
				.build();

		refreshTokenRepository.save(token);
	}
	
	// 리프레시 토큰 삭제
	@Override
	public void removeRefreshToken(String token) {
		// Token에서 userNo 추출
        int userNo = jwtUtil.getUserNoFromToken(token);
        
        // RefreshToken 테이블에서 해당 유저의 토큰 삭제
        removeRefreshTokenByUserNo(userNo);
	}
	
	// 회원번호로 리프레시 토큰 DB에서 삭제
	@Override
	public void removeRefreshTokenByUserNo(int userNo) {
        refreshTokenRepository.deleteByUserNo(userNo);
	}

	// 로그인
	@Override
	public Map<String, Object> login(User user) {

		// DB에서 해당 이메일 가진 회원 조회
		User loginUser = userMapper.login(user.getUserEmail());

		// 비밀번호 검증
		if (!bCryptPasswordEncoder.matches(user.getUserPw(), loginUser.getUserPw())) {
			throw new RuntimeException("Invalid password");
		}

		// 토큰발급
		String accessToken = jwtUtil.generateAccessToken(loginUser.getUserNo(), loginUser.getUserEmail());
		String refreshToken = jwtUtil.generateRefreshToken(loginUser.getUserNo(), loginUser.getUserEmail());

		// accessToken/refreshToken은 쿠키로
		// refreshToken은 DB에 삽입
		saveRefreshToken(loginUser.getUserNo(), refreshToken);

		loginUser.setUserPw(null);

		Map<String, Object> map = new HashMap<>();
		map.put("loginUser", loginUser);
		map.put("accessToken", accessToken);
		map.put("refreshToken", refreshToken);

		// 유저정보 & 토큰 반환
		return map;

	}

	
}
