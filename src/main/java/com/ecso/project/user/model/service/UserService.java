package com.ecso.project.user.model.service;

import java.util.Map;

import com.ecso.project.user.model.dto.User;

public interface UserService {

	String sendAuthKey(String userEmail);

	int signUp(User user);

}
