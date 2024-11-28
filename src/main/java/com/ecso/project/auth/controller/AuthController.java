package com.ecso.project.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecso.project.auth.model.dto.SessionManager;
import com.ecso.project.auth.model.service.AuthService;
import com.ecso.project.common.util.CookieUtil;
import com.ecso.project.common.util.JwtUtil;
import com.ecso.project.user.model.dto.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") // React 앱의 주소
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
@Slf4j
public class AuthController {

	private final AuthService authService;
	private final JwtUtil jwtUtil;
	private final CookieUtil cookieUtil;

	@Autowired
	private SessionManager sessionManager;

	// 로그인
	@PostMapping("login")
	public User login(@RequestBody User user, HttpServletResponse resp) {

		Map<String, Object> map = authService.login(user);

		Cookie accessToken = cookieUtil.generateCookie("ecso-at", 
				(String) map.get("accessToken"), 
				true,
				30); // 30분
		
		Cookie refreshToken = cookieUtil.generateCookie("ecso-rt", 
				(String) map.get("refreshToken"), 
				true, 
				1209600); // 2주

		// 로그인 유저 정보를 세션에 저장
		User loginUser = (User) map.get("loginUser");
		sessionManager.setLoginUser(loginUser);

		resp.addCookie(accessToken);
		resp.addCookie(refreshToken);
		
		return User.builder().
				userName(loginUser.getUserName())
				.rankTitle(loginUser.getRankTitle()).build();
	}

	// 로그아웃
	@PostMapping("logout")
	public ResponseEntity<String> logout(HttpSession session,
            HttpServletResponse resp,
            @CookieValue(name = "ecso-at", required = false) String accessToken,
            @CookieValue(name = "ecso-rt", required = false) String refreshToken
            ) {
		
		// 1. 쿠키 삭제 처리
	    Cookie accessTokenCookie = cookieUtil.generateCookie("ecso-at", null, true, 0);
	    Cookie refreshTokenCookie = cookieUtil.generateCookie("ecso-rt", null, true, 0);
	    
	    resp.addCookie(accessTokenCookie);
	    resp.addCookie(refreshTokenCookie);

	    // 2. DB의 Refresh Token 삭제
	    if (accessToken != null) {
	        try {
	            authService.removeRefreshToken(accessToken);
	        } catch (Exception e) {
	            // 토큰이 만료되었거나 유효하지 않은 경우에도 계속 진행
	            log.warn("Access Token이 만료되거나 유효하지 않음: {}", accessToken, e);
	        }
	    } else if(refreshToken != null) {
	    	try {
	            authService.removeRefreshToken(refreshToken);
	        } catch (Exception e) {
	        	log.warn("Refresh Token이 만료되거나 유효하지 않음: {}", refreshToken, e);
	        }
	    } else {
	        // accessToken 없는 경우, 현재 세션이나 다른 식별자를 통해 
	        // 해당 유저의 refreshToken을 찾아서 삭제하는 대체 로직
	        int userNo = sessionManager.getLoginUser().getUserNo();
	        if (userNo != 0) authService.removeRefreshTokenByUserNo(userNo);
	        
	    }

	    // 3. 세션 정리
	    sessionManager.logout();
	    return ResponseEntity.ok().build();
	}
	
	// 토큰갱신
	@PostMapping("refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(name = "ecso-rt", required = false) String refreshToken,
            HttpServletResponse resp ) {
        
		log.debug("인터셉터 쿠키에서 가져온 refreshToken : " + refreshToken);
		// refresh token이 없는 경우
        if (refreshToken.equals("")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token이 없습니다.");
        }

    	// refresh token이 있는 경우
        try {
            // refresh token 검증
            User user = authService.validateRefreshToken(refreshToken);
            
            if(user != null) {
            	// 새 access token 발급
            	 String newAccessToken = jwtUtil.generateAccessToken(user.getUserNo(), user.getUserEmail());
            	 
            	  // 새 access token을 쿠키에 설정
                 Cookie accessToken = cookieUtil.generateCookie("ecso-at", 
                 		newAccessToken, 
         				true, 
         				30); // 30분
                 
                 log.debug("새로 발급된 accessToken : " + newAccessToken);
                 resp.addCookie(accessToken);
                 
                 return ResponseEntity.ok().build();
                 
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 Refresh token");

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token은 만료됨.");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 Refresh token");
        }
    }
}
