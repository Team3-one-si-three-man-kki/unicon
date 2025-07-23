package com.demo.proworks.user.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.demo.proworks.user.service.SignupService;
import com.demo.proworks.user.vo.UserSignupVo;
import com.demo.proworks.user.service.impl.UserServiceImpl;
import com.demo.proworks.user.vo.UserVo;
import com.demo.proworks.util.PasswordEncryptUtil;
import com.inswave.elfw.log.AppLog;
import com.demo.proworks.user.service.UserService;
import com.demo.proworks.tenant.vo.TenantVo;
import com.demo.proworks.tenant.service.LoginPageService;
import com.demo.proworks.tenant.service.TenantService;

/**
 * 회원가입 서비스 구현체
 * 기존 UserService, TenantService 인터페이스를 활용하여 회원가입 비즈니스 로직 구현
 */
@Service("signupServiceImpl")
public class SignupServiceImpl implements SignupService {
    
    
    @Autowired
    private UserService userService; // 인터페이스로 주입
    
    @Autowired
    private TenantService tenantService; // 인터페이스로 주입
    
    @Autowired
    private LoginPageService loginPageService;
    
    @Autowired
    private PasswordEncryptUtil passwordEncryptUtil;
    
    /**
     * 회원가입 처리 메인 메서드
     * 트랜잭션으로 tenant → user 순차 처리
     */
    @Override
    @Transactional
    public UserVo processSignup(UserSignupVo signupRequest) throws Exception {
    try {
        // 1. 유효성 검증
        validateSignupRequest(signupRequest);

        // 2. 이메일 중복 검사
        if (!isEmailAvailable(signupRequest.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + signupRequest.getEmail());
        }

        // 3. 서브도메인 중복 검사
        if (!isSubDomainAvailable(signupRequest.getSubDomain())) {
            throw new IllegalArgumentException("이미 사용 중인 서브도메인입니다: " + signupRequest.getSubDomain());
        }

        // 4. Tenant 생성
        TenantVo tenant = createTenantInfo(signupRequest);
        tenantService.insertTenant(tenant);       

        // 5. 비밀번호 처리
        if ("kakao".equals(signupRequest.getLoginType())) {
            // NULL 허용 스키마라면 그대로 null 설정
            signupRequest.setPassword(null);
        } else {
            // 일반 회원가입
            signupRequest.setPassword(passwordEncryptUtil.encryptPassword(signupRequest.getPassword()));
        }
        
        // 로그인 페이지 정보 생성
        loginPageService.newLoginPageConfig(Integer.parseInt(tenant.getTenantId()));
        
        // 6. User 생성
        UserVo user = createUserInfo(signupRequest, tenant.getTenantId());
        userService.insertUser(user);

        // 7. 응답용 비밀번호 제거
        user.setPassword(null);
        return user;

    } catch (Exception e) {
        throw new Exception("회원가입 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
    }
}
    
    /**
     * 이메일 사용 가능 여부 검사
     */
    @Override
    public boolean isEmailAvailable(String email) {
    if (isEmpty(email)) {
        return false;
    }
    
    try {
        UserVo existingUser = userService.getUserByEmail(email);
        System.out.println("이메일 사용가능여부 검사: " + existingUser);
        
        return existingUser == null;
        
    } catch (Exception e) {
        System.err.println("이메일 중복 검사 중 오류: " + e.getMessage());
        return false; // 오류 시 안전하게 false 반환
    }
}
    
    /**
     * 서브도메인 사용 가능 여부 검사
     */
    @Override
    public boolean isSubDomainAvailable(String subDomain) {
        if (isEmpty(subDomain)) {
            return false;
        }
        
        try {
            TenantVo searchVo = new TenantVo();
            searchVo.setScSubDomain(subDomain); // 검색 조건 설정
            
            List<TenantVo> existingTenants = tenantService.selectListTenant(searchVo);
            
            return existingTenants == null || existingTenants.isEmpty();
            
        } catch (Exception e) {
            System.err.println("서브도메인 중복 검사 중 오류: " + e.getMessage());
            return false; // 오류 시 안전하게 false 반환
        }
    }
    
    /**
     * 회원가입 요청 데이터 유효성 검증
     */
    @Override
   public void validateSignupRequest(UserSignupVo signupRequest) {
    if (signupRequest == null) {
        throw new IllegalArgumentException("회원가입 요청 정보가 없습니다.");
    }
    // 공통 필수 검증
    if (isEmpty(signupRequest.getTenantName())) {
        throw new IllegalArgumentException("테넌트명은 필수입니다.");
    }
    if (isEmpty(signupRequest.getSubDomain())) {
        throw new IllegalArgumentException("서브도메인은 필수입니다.");
    }
    if (isEmpty(signupRequest.getUserName())) {
        throw new IllegalArgumentException("사용자명은 필수입니다.");
    }
    if (isEmpty(signupRequest.getEmail())) {
        throw new IllegalArgumentException("이메일은 필수입니다.");
    }

    // 카카오 회원가입이면 비밀번호 검증 스킵
    if (!"kakao".equals(signupRequest.getLoginType())) {
        if (isEmpty(signupRequest.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if (isEmpty(signupRequest.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호 확인은 필수입니다.");
        }
        if (!signupRequest.getPassword().equals(signupRequest.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
        if (signupRequest.getPassword().length() < 6) {
            throw new IllegalArgumentException("비밀번호는 6자 이상이어야 합니다.");
        }
    }

    // 이메일 형식 검증
    if (!isValidEmail(signupRequest.getEmail())) {
        throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
    }

    // 서브도메인 형식 검증
    if (!isValidSubDomain(signupRequest.getSubDomain())) {
        throw new IllegalArgumentException("서브도메인은 영문자, 숫자, 하이픈만 사용 가능합니다.");
    }
}
    
    /**
     * Tenant 정보 생성 (private 메서드)
     */
    private TenantVo createTenantInfo(UserSignupVo signupRequest) {
        TenantVo tenant = new TenantVo();
        tenant.setName(signupRequest.getTenantName());
        tenant.setSubDomain(signupRequest.getSubDomain());
        tenant.setIsActive("1"); // 활성 상태로 설정
        tenant.setCreatedAt(getCurrentDateTimeString());
        
        return tenant;
    }
    
    /**
     * User 정보 생성 (private 메서드)
     */
    private UserVo createUserInfo(UserSignupVo signupRequest, String tenantId) {
        UserVo user = new UserVo();
        user.setTenantId(tenantId); // AUTO_INCREMENT로 생성된 tenant_id 설정
        user.setName(signupRequest.getUserName());
        user.setEmail(signupRequest.getEmail());
        
        // 비밀번호 암호화
        //String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        user.setPassword(signupRequest.getPassword());
        
        // 역할 설정 (기본값 또는 요청값)
        user.setRole("manager");
        
        user.setCreatedAt(getCurrentDateTimeString());
        
        return user;
    }
    
    // 유틸리티 메서드들
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    private boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        
        // 간단한 이메일 형식 검증
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailPattern);
    }
    
    private boolean isValidSubDomain(String subDomain) {
        if (isEmpty(subDomain)) {
            return false;
        }
        
        // 서브도메인 형식 검증: 영문자, 숫자, 하이픈만 허용
        String subDomainPattern = "^[a-zA-Z0-9-]+$";
        return subDomain.matches(subDomainPattern) && 
               !subDomain.startsWith("-") && 
               !subDomain.endsWith("-");
    }
    
    private String getCurrentDateTimeString() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
    
}
