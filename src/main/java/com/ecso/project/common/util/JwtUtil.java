package com.ecso.project.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity; // 1시간
    
    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity; // 2주
    
    @Value("${jwt.verification-token-validity}")
    private long verificationTokenValidity; // 5분 (임시용)
    
    

    // 비밀키 생성
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Access Token 생성
    public String generateAccessToken(int userNo, String userEmail) {
        return generateToken(userNo, userEmail, accessTokenValidity);
    }

    // Refresh Token 생성
    public String generateRefreshToken(int userNo, String userEmail) {
        return generateToken(userNo, userEmail, refreshTokenValidity);
    }
    
	// Verification Token 생성(임시발급용)
	/*public String generateVerificationToken(String userEmail) {
		return generateToken(userEmail, null, verificationTokenValidity);
	}*/

    // 토큰 생성 메소드
    private String generateToken(int userNo, String userEmail, long validity) {
        Map<String, Object> claims = new HashMap<>();
        
        claims.put("userNo", userNo);
        claims.put("userEmail", userEmail);

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiration = new Date(nowMillis + validity);

        return Jwts.builder()
                .claims(claims)  
                .issuedAt(now) 
                .expiration(expiration) 
                .signWith(getSigningKey())
                .compact();
    }

    // 토큰에서 이메일 추출
    public String getUserEmailFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("userEmail", String.class);
    }
    
    // 토큰에서 회원번호 추출
    public int getUserNoFromToken(String token) {
    	Claims claims = getAllClaimsFromToken(token);
    	return claims.get("userNo", Integer.class);
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰에서 모든 클레임 추출
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}