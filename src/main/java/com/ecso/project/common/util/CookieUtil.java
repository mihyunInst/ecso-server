package com.ecso.project.common.util;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;

@Component
public class CookieUtil {
	
	public Cookie generateCookie(String name, String value, boolean httpOnly, int maxAge) {
		
		Cookie cookie = new Cookie(name, value);
		
		cookie.setHttpOnly(httpOnly);
		cookie.setSecure(true);
		cookie.setMaxAge(maxAge);
		cookie.setPath("/");
		
		return cookie;
	}
}
