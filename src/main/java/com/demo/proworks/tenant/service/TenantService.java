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
public interface TenantService {
	
    /**
     * 테넌트 페이징 처리하여 목록을 조회한다.
     *
     * @param  tenantVo 테넌트 TenantVo
     * @return 테넌트 목록 List<TenantVo>
     * @throws Exception
     */
	public List<TenantVo> selectListTenant(TenantVo tenantVo) throws Exception;
	
    /**
     * 조회한 테넌트 전체 카운트
     * 
     * @param  tenantVo 테넌트 TenantVo
     * @return 테넌트 목록 전체 카운트
     * @throws Exception
     */
	public long selectListCountTenant(TenantVo tenantVo) throws Exception;
	
    /**
     * 테넌트를 상세 조회한다.
     *
     * @param  tenantVo 테넌트 TenantVo
     * @return 단건 조회 결과
     * @throws Exception
     */
	public TenantVo selectTenant(TenantVo tenantVo) throws Exception;
		
    /**
     * 테넌트를 등록 처리 한다.
     *
     * @param  tenantVo 테넌트 TenantVo
     * @return 번호
     * @throws Exception
     */
	public int insertTenant(TenantVo tenantVo) throws Exception;
	
    /**
     * 테넌트를 갱신 처리 한다.
     *
     * @param  tenantVo 테넌트 TenantVo
     * @return 번호
     * @throws Exception
     */
	public int updateTenant(TenantVo tenantVo) throws Exception;
	
    /**
     * 테넌트를 삭제 처리 한다.
     *
     * @param  tenantVo 테넌트 TenantVo
     * @return 번호
     * @throws Exception
     */
	public int deleteTenant(TenantVo tenantVo) throws Exception;
	
}
