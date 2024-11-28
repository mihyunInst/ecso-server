package com.ecso.project.auth.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecso.project.auth.model.dto.RefreshToken;

//RefreshTokenRepository.java
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
	
	Optional<RefreshToken> findByUserNo(Long userNo);

	void deleteByUserNo(int userNo);
}
