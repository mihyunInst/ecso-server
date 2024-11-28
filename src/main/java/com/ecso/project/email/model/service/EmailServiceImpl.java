package com.ecso.project.email.model.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecso.project.email.model.mapper.EmailMapper;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
	// 의존성 주입될 객체들
	private final JavaMailSender mailSender; // 실제 메일 발송을 담당하는 객체(EmailConfig 설정이 적용된 객체(메일 보내기 기능))
	private final EmailMapper mapper;

	public String sendEmail(String htmlName, String email) {

		// 1. 인증키 생성 및 DB 저장 준비
		String authKey = createAuthKey();
		log.debug("authKey: " + authKey);

		Map<String, String> map = new HashMap<>();
		map.put("authKey", authKey);
		map.put("email", email);

		// DB 저장 시도 - 실패시 바로 종료
		if (!storeAuthKey(map)) {
			return null;
		}

		try {
			// 2. DB 저장 성공한 경우에만 메일 발송 시도
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			// 메일 발송을 도와주는 Helper 클래스 (파일첨부, 템플릿 설정 등을 쉽게 처리)
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			// 메일 기본 정보 설정
			helper.setTo(email); // 받는 사람
			helper.setSubject("[ECSO] 회원 가입 인증번호 발급 안내");
			helper.setText(authKey);

			// 실제 메일 발송
			mailSender.send(mimeMessage);
			return authKey; // 모든 작업 성공시 인증키 반환

		} catch (Exception e) {
			e.printStackTrace();
			return null; // 메일 발송 실패 시 null 반환
		}
	}

	// 인증키를 DB에 저장하는 메서드
	@Transactional(rollbackFor = Exception.class) // 메서드 레벨에서도 사용 가능(해당메서드에서만 트랜잭션 커밋/롤백처리)
	public boolean storeAuthKey(Map<String, String> map) {
		// 1. 기존 이메일에 대한 인증키 update 시도
		int result = mapper.updateAuthKey(map);

		// 2. update 실패(== 기존 데이터 없음)시 insert 시도
		if (result == 0) {
			result = mapper.insertAuthKey(map);
		}

		return result > 0; // 성공 여부 반환 (true/false)
	}

	// UUID를 사용하여 인증키 생성 (간단하고 안전한 방식)
	// (참고) UUID(Universally Unique Identifier)는 전 세계에서 고유한 식별자를 생성하기 위한 표준
	// 128비트 길이의 숫자로 구성되어 있어 매우 낮은 확률로 중복되는 식별자를 생성
	// 주로 데이터베이스 기본 키, 고유한 식별자를 생성해야 할 때 사용
	private String createAuthKey() {
		return UUID.randomUUID().toString().substring(0, 6);
	}

	@Override
	public int checkAuthKey(Map<String, String> map) {
		return mapper.checkAuthKey(map);
	}
	
	
}
