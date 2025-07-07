package com.demo.proworks.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BCrypt 암호화 유틸리티 클래스
 * 프로웍스 시스템 로그인 및 회원가입 처리용
 */
@Component
public class PasswordEncryptUtil {
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    /**
     * 생성자 - BCrypt Work Factor 12 적용 (높은 보안 강도)
     */
    public PasswordEncryptUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }
    
    /**
     * 패스워드 암호화
     * 
     * @param plainPassword 평문 패스워드
     * @return 암호화된 패스워드 (60자리 해시)
     */
    public String encryptPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("패스워드는 null이거나 빈 문자열일 수 없습니다.");
        }
        
        String encryptedPassword = passwordEncoder.encode(plainPassword);
        System.out.println("패스워드 암호화 완료 - 길이: " + encryptedPassword.length() + "자");
        
        return encryptedPassword;
    }
    
    /**
     * 패스워드 검증
     * 
     * @param plainPassword 평문 패스워드 (사용자 입력)
     * @param encryptedPassword 암호화된 패스워드 (DB 저장값)
     * @return 일치 여부
     */
    public boolean verifyPassword(String plainPassword, String encryptedPassword) {
        if (plainPassword == null || encryptedPassword == null) {
            throw new IllegalArgumentException("패스워드 값은 null일 수 없습니다.");
        }
        
        boolean isMatch = passwordEncoder.matches(plainPassword, encryptedPassword);
        System.out.println("패스워드 검증 결과: " + (isMatch ? "성공" : "실패"));
        
        return isMatch;
    }
    
    /**
     * BCrypt 해시 유효성 확인
     */
    public boolean isValidBCryptHash(String encryptedPassword) {
        if (encryptedPassword == null) {
            return false;
        }
        return encryptedPassword.matches("^\\$2[ayb]\\$.{56}$");
    }
}
