package com.demo.proworks.user.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.security.crypto.password.PasswordEncoder;

import com.demo.proworks.user.service.SignupService;
import com.demo.proworks.user.vo.UserSignupVo;
import com.demo.proworks.user.service.impl.UserServiceImpl;
import com.demo.proworks.user.vo.UserVo;
import com.demo.proworks.user.service.UserService;
import com.demo.proworks.tenant.vo.TenantVo;
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
    
   // @Autowired
    //private PasswordEncoder passwordEncoder;
    
    /**
     * 회원가입 처리 메인 메서드
     * 트랜잭션으로 tenant → user 순차 처리
     */
    @Override
    @Transactional
    public UserVo processSignup(UserSignupVo signupRequest) throws Exception {
        try {
            // 1. 입력 데이터 유효성 검증
            validateSignupRequest(signupRequest);
            
            // 2. 이메일 중복 검사
            if (!isEmailAvailable(signupRequest.getEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + signupRequest.getEmail());
            }
            
            // 3. 서브도메인 중복 검사
            if (!isSubDomainAvailable(signupRequest.getSubDomain())) {
                throw new IllegalArgumentException("이미 사용 중인 서브도메인입니다: " + signupRequest.getSubDomain());
            }
            
            // 4. Tenant 정보 생성 및 저장
            TenantVo tenant = createTenantInfo(signupRequest);
            tenantService.insertTenant(tenant); // AUTO_INCREMENT ID가 tenant 객체에 설정됨
            
            System.out.println("테넌트 생성 완료 - ID: " + tenant.getTenantId());
            
            // 5. User 정보 생성 및 저장 (생성된 tenant_id 사용)
            UserVo user = createUserInfo(signupRequest, tenant.getTenantId());
            userService.insertUser(user); // AUTO_INCREMENT ID가 user 객체에 설정됨
            
            System.out.println("사용자 생성 완료 - ID: " + user.getUserId() + 
                             ", 테넌트ID: " + user.getTenantId());
            
            // 6. 패스워드 정보는 응답에서 제거
            user.setPassword(null);
            
            return user;
            
        } catch (Exception e) {
            System.err.println("회원가입 처리 중 오류 발생: " + e.getMessage());
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
            UserVo searchVo = new UserVo();
            searchVo.setEmail(email); // 검색 조건 설정
            
            UserVo existingUsers = userService.loginUser(searchVo);
            System.out.println("이메일 사용가능여부 검사: "+existingUsers);
            
            return existingUsers == null;
            
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
    public void validateSignupRequest(UserSignupVo signupRequest) throws IllegalArgumentException {
        if (signupRequest == null) {
            throw new IllegalArgumentException("회원가입 요청 정보가 없습니다.");
        }
        
        // 필수 필드 검증
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
        
        if (isEmpty(signupRequest.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        
        if (isEmpty(signupRequest.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호 확인은 필수입니다.");
        }
        
        // 비밀번호 일치 검증
        if (!signupRequest.getPassword().equals(signupRequest.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
        
        // 이메일 형식 검증
        if (!isValidEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }
        
        // 비밀번호 길이 검증
        if (signupRequest.getPassword().length() < 6) {
            throw new IllegalArgumentException("비밀번호는 6자 이상이어야 합니다.");
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
