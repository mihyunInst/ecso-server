package com.ecso.project.auth.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecso.project.auth.model.service.AuthService;
import com.ecso.project.common.util.CookieUtil;
import com.ecso.project.user.model.dto.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") // React 앱의 주소
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
@Slf4j
public class AuthController {

	private final AuthService authService;
	private final CookieUtil cookieUtil;
	private final ObjectMapper objectMapper;


	@PostMapping("login")
	public ResponseEntity<String> login(@RequestBody User user, HttpServletResponse resp) throws JsonProcessingException {
		Map<String, Object> map = authService.login(user);
		
		Cookie accessToken = cookieUtil.generateCookie("accessToken", 
				(String) map.get("accessToken"), 
				true, 
				3600);  // 1시간
		
		Cookie refreshToken = cookieUtil.generateCookie("refreshToken", 
				(String) map.get("refreshToken"), 
				true, 
				1209600); // 2주
		
		// User 객체를 JSON 문자열로 변환
		String loginUser = objectMapper.writeValueAsString(map.get("loginUser"));
		
		// JSON 문자열을 URL 인코딩
		// 쿠키에는 특정 문자(따옴표, 공백, 쉼표 등)를 직접 저장할 수 없음. 
		// -> JSON 문자열을 쿠키에 저장하기 위해서는 인코딩이 필요
        String encodedUserJson = URLEncoder.encode(loginUser, StandardCharsets.UTF_8);
		
		Cookie userInfo = cookieUtil.generateCookie("userInfo", 
				encodedUserJson, 
				false,  // JavaScript에서 읽을 수 있도록 설정
				1209600); // 2주
		
		
		resp.addCookie(accessToken);
		resp.addCookie(refreshToken);
		resp.addCookie(userInfo);

	    return ResponseEntity.ok().build();
	}
	
	@PostMapping("sendVerification")
	public ResponseEntity<String> sendAuthKey(@RequestBody User user, HttpServletResponse resp) {
		
		String emailAuthTokenKey = authService.sendAuthKey(user.getUserEmail());
		
		if(emailAuthTokenKey != null) {
			
			Cookie emailAuthToken = cookieUtil.generateCookie("emailAuthToken", 
												emailAuthTokenKey, 
												true,  
												300); // 5분
			
			resp.addCookie(emailAuthToken);
			
		}
		
		return ResponseEntity.ok(emailAuthTokenKey);
		
	}

	@PostMapping("checkEmailAuthKey")
	public int checkEmailAuthKey(@RequestBody Map<String, String> map,
					            @CookieValue(name = "emailAuthToken", required = false) String token) {
		
		 String verificationCode = map.get("verificationCode");  // 프론트에서 보낸 key 이름과 일치시켜야 함
	    log.debug("인증번호 : {}", verificationCode);
	    log.debug("token : " + token);
	    
	    int result = authService.checkAuthKey(verificationCode, token);
	
		return result;
	}
	
	@PostMapping("signUp")
	public int signUp(@RequestBody User user,
			 @CookieValue(name = "emailAuthToken", required = false) String token
			) {
		log.debug("user : " + user);
		log.debug("token : " + token);
		// 쿠키로 넘어온 토큰 확인하여 만료되지 않았고 확인이 된다면 회원가입 진행
		// 전달받은 user 정보 DB에 삽입
		return 0;
	}

}
