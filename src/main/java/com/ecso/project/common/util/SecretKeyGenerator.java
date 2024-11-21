package com.ecso.project.common.util;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
    public static void main(String[] args) {
        // 32바이트(256비트) 랜덤 키 생성
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        
        // Base64 인코딩
        String secretKey = Base64.getEncoder().encodeToString(bytes);
        System.out.println("Generated Secret Key: " + secretKey);
    }
}