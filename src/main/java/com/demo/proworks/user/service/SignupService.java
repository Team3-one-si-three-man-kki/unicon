package com.demo.proworks.user.service;

import com.demo.proworks.user.vo.UserSignupVo;
import com.demo.proworks.user.vo.UserVo;

/**
 * 회원가입 서비스 인터페이스
 * 회원가입 관련 비즈니스 로직 정의
 */
public interface SignupService {
    
    
    
    /**
     * 회원가입 처리
     * @param signupRequest 회원가입 요청 정보
     * @return 생성된 사용자 정보
     * @throws Exception 회원가입 처리 중 오류 발생 시
     */
   public UserVo processSignup(UserSignupVo signupRequest) throws Exception;
    
    /**
     * 이메일 중복 검사
     * @param email 검사할 이메일
     * @return 사용 가능하면 true, 중복이면 false
     */
    public boolean isEmailAvailable(String email);
    
    /**
     * 서브도메인 중복 검사
     * @param subDomain 검사할 서브도메인
     * @return 사용 가능하면 true, 중복이면 false
     */
    public  boolean isSubDomainAvailable(String subDomain);
    
    /**
     * 회원가입 요청 데이터 유효성 검증
     * @param signupRequest 검증할 회원가입 요청 정보
     * @throws IllegalArgumentException 유효하지 않은 데이터가 있을 경우
     */
    public void validateSignupRequest(UserSignupVo signupRequest) throws IllegalArgumentException;
    
    
}
