package com.ecso.project.user.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.ecso.project.user.model.dto.User;

@Mapper
public interface UserMapper {

	User login(String userEmail);

	int checkDupEmail(String email);
	
}
