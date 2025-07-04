package com.demo.proworks.tenant.service;

import java.util.List;

import com.demo.proworks.tenant.vo.TenantVo;

/**  
 * @subject     : 테넌트 관련 처리를 담당하는 인터페이스
 * @description : 테넌트 관련 처리를 담당하는 인터페이스
 * @author      : LEEBYUNGWOOK
 * @since       : 2025/07/01
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/01			 LEEBYUNGWOOK	 		최초 생성
 * 
 */
public interface LoginPageService {
	
    /**
     * 테넌트 페이징 처리하여 목록을 조회한다.
     *
     * @param  tenantVo 테넌트 TenantVo
     * @return 테넌트 목록 List<TenantVo>
     * @throws Exception
     */
	 public String getConfigJsonBySubDomain(String subDomain) throws Exception;
	
   
	
}
