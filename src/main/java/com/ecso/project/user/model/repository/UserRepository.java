package com.ecso.project.user.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecso.project.user.model.dto.User;

//@Repository
public interface UserRepository/*extends JpaRepository<User, Long> */{

	// JPA 메소드 이름 규칙
	// - 기본구조 : find + [조건] + By + [필드명] + [비교조건]
	// find...By : 조회
	// exists...By : 존재 여부
	// count...By : 카운트
	// delete...By : 삭제
	// ...First<number>... : 처음 number개
	// ...Top<number>... : 상위 number개
	//User findByUserEmail(String userEmail);

}
