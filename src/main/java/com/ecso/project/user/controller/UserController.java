package com.ecso.project.user.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecso.project.auth.model.dto.SessionManager;
import com.ecso.project.common.util.JwtUtil;
import com.ecso.project.email.model.service.EmailService;
import com.ecso.project.user.model.dto.User;
import com.ecso.project.user.model.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") // React 앱의 주소
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final UserService userService;
	private final EmailService emailService;
	private final JwtUtil jwtutil;

	@Autowired
	private SessionManager sessionManager;

	// 로그인된 사용자의 기본 정보 조회
	@GetMapping("info")
	public ResponseEntity<?> getUserInfo(@CookieValue(name = "ecso-at", required = false) String accessToken) {
		

	    // 1. 토큰이 없는 경우 즉시 401 반환
	    if (accessToken == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("accessToken 토큰 없음");
	    }

	    try {
	        // 2. 토큰 검증 - 실패시 예외 발생
	        if (!jwtutil.validateToken(accessToken)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("accessToken이 유효하지 않음");
	        }

	        // 3. 세션 확인
	        User loginUser = sessionManager.getLoginUser();

	        if (loginUser == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션에 loginUser 없음");
	        }

	        // 4. 응답 데이터 생성
	        User user = User.builder()
	                .userNo(loginUser.getUserNo())
	                .userName(loginUser.getUserName())
	                .userEmail(loginUser.getUserEmail())
	                .classTerm(loginUser.getClassTerm())
	                .rankTitle(loginUser.getRankTitle())
	                .build();

	        return ResponseEntity.ok(user);

	    } catch (ExpiredJwtException e) {
	        // 토큰 만료
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
	    } catch (Exception e) {
	        // 기타 토큰 관련 에러
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
	    }
	}

	// 이메일 중복검사 & 이메일 인증번호 발급
	@PostMapping("sendVerification")
	public String sendAuthKey(@RequestBody User user) {

		return userService.sendAuthKey(user.getUserEmail());
	}

	// 이메일과 인증번호 확인
	@PostMapping("checkEmailAuthKey")
	public int checkEmailAuthKey(@RequestBody Map<String, String> map) {
		return emailService.checkAuthKey(map);
	}

	// 회원가입 (휴대폰번호 중복검사)
	@PostMapping("signUp")
	public int signUp(@RequestBody User user) {

		int result = userService.signUp(user);

		return result;
	}

	// 마이페이지용
}
