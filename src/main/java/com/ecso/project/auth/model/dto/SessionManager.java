package com.ecso.project.auth.model.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecso.project.user.model.dto.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SessionManager {

	@Autowired
	private HttpServletRequest request;

	public void setLoginUser(User user) {
		log.debug("setLoginUser - loginUser : {}", user);
		request.getSession().setAttribute("loginUser", user);
	}

	public User getLoginUser() {
		log.debug("getLoginUser - loginUser : {}", (User) request.getSession().getAttribute("loginUser"));
		return (User) request.getSession().getAttribute("loginUser");
	}

	public void logout() {
		request.getSession().invalidate();
	}
}
