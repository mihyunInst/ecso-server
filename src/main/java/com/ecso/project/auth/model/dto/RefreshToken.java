package com.ecso.project.auth.model.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Entity
@Table(name = "REFRESH_TOKEN")
@Getter
@NoArgsConstructor
public class RefreshToken {

	@Id
	@Column(name = "USER_NO")
	private int userNo;

	@Column(name = "REFRESH_TOKEN", nullable = false)
	private String refreshToken;

	@Column(name = "ISSUED_AT", nullable = false)
	private LocalDateTime issuedAt;

	@Column(name = "EXPIRES_AT", nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "IS_REVOKED", nullable = false)
	private Integer isRevoked = 0;

	@Builder
	public RefreshToken(int userNo, String refreshToken, LocalDateTime expiresAt) {
		this.userNo = userNo;
		this.refreshToken = refreshToken;
		this.issuedAt = LocalDateTime.now();
		this.expiresAt = expiresAt;
	}
}
