package com.ecso.project.user.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ecso.project.user.model.dto.User;

@Mapper
public interface UserMapper {

	User login(String userEmail);

	int checkDupEmail(String email);

	int signUp(User user);

	int checkDupPhone(String userPhone);

	User getUserByEmail(String email);
	
}
