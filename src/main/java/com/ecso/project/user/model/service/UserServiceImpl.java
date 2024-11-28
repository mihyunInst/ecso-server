package com.ecso.project.user.model.service;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecso.project.email.model.service.EmailService;
import com.ecso.project.user.model.dto.User;
import com.ecso.project.user.model.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserMapper userMapper;
	private final EmailService emailService;
	

	@Override
	public String sendAuthKey(String email) {

		// DB에서 해당 이메일 가진 회원이 있는지 검사(이메일 중복검사)
		int result = userMapper.checkDupEmail(email);

		if (result > 0) return null; // 중복이면 리턴

		// 중복 아닐때
		// 인증번호 발급 및 DB저장,이메일전송
		String authKey = emailService.sendEmail("signUp", email);

		return authKey;
	}
	
	// 회원가입
	@Override
	public int signUp(User user) {
		
		// 휴대폰 중복 검사
		int result = userMapper.checkDupPhone(user.getUserPhone());
		
		if(result > 0) {
			return 2;
		}
		
		// 비밀번호 암호화
		String encPw = bCryptPasswordEncoder.encode(user.getUserPw());
		user.setUserPw(encPw);
		
		return userMapper.signUp(user);
	}


	
}
