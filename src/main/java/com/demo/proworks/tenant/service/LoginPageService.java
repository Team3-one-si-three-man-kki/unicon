package com.demo.proworks.tenant.service;

import java.util.List;

import com.demo.proworks.tenant.vo.LoginPageVo;
import com.demo.proworks.tenant.vo.TenantVo;

/**  
 * @subject     : 로그인 커스텀 페이지 설정 처리를 담당하는 인터페이스
 * @description : 로그인 커스텀 페이지 설정 처리를 담당하는 인터페이스
 * @author      : LEEJAEWON
 * @since       : 2025/07/04
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/04			 LEEJAEWON	 		최초 생성
 * 
 */
public interface LoginPageService {
	
    /**
     * 로그인 커스텀 페이지 설정을 조회한다.
     *
     * @param  subDomain subDomain String
     * @return 설정 정보 String
     * @throws Exception
     */
	 public String getConfigJsonBySubDomain(String subDomain) throws Exception;
	 
	 
	 
	 /**
     * 로그인 커스텀 페이지 설정을 저장한다.
     *
     * @param  configJson configJson String
     * @return 
     * @throws Exception
     */
	 public int saveLoginPageConfig(LoginPageVo vo) throws Exception;
	
   
	
}
