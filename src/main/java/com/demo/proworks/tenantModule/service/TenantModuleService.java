package com.demo.proworks.tenantModule.service;

import java.util.List;

import com.demo.proworks.tenantModule.vo.TenantModuleVo;
import com.demo.proworks.tenantModule.vo.TenantModuleDetailVo;

/**  
 * @subject     : 테넌트모듈정보 관련 처리를 담당하는 인터페이스
 * @description : 테넌트모듈정보 관련 처리를 담당하는 인터페이스
 * @author      : 여경원
 * @since       : 2025/07/14
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/14			 여경원	 		최초 생성
 * 
 */
public interface TenantModuleService {
	
    /**
     * 테넌트모듈정보 페이징 처리하여 목록을 조회한다.
     *
     * @param  tenantModuleVo 테넌트모듈정보 TenantModuleVo
     * @return 테넌트모듈정보 목록 List<TenantModuleVo>
     * @throws Exception
     */
	public List<TenantModuleVo> selectListTenantModule(TenantModuleVo tenantModuleVo) throws Exception;
	
    /**
     * 조회한 테넌트모듈정보 전체 카운트
     * 
     * @param  tenantModuleVo 테넌트모듈정보 TenantModuleVo
     * @return 테넌트모듈정보 목록 전체 카운트
     * @throws Exception
     */
	public long selectListCountTenantModule(TenantModuleVo tenantModuleVo) throws Exception;
	
    /**
     * 테넌트모듈정보를 상세 조회한다.
     *
     * @param  tenantModuleVo 테넌트모듈정보 TenantModuleVo
     * @return 단건 조회 결과
     * @throws Exception
     */
	public TenantModuleVo selectTenantModule(TenantModuleVo tenantModuleVo) throws Exception;
		
    /**
     * 테넌트모듈정보를 등록 처리 한다.
     *
     * @param  tenantModuleVo 테넌트모듈정보 TenantModuleVo
     * @return 번호
     * @throws Exception
     */
	public int insertTenantModule(TenantModuleVo tenantModuleVo) throws Exception;
	
    /**
     * 테넌트모듈정보를 갱신 처리 한다.
     *
     * @param  tenantModuleVo 테넌트모듈정보 TenantModuleVo
     * @return 번호
     * @throws Exception
     */
	public int updateTenantModule(TenantModuleVo tenantModuleVo) throws Exception;
	
    /**
     * 테넌트모듈정보를 삭제 처리 한다.
     *
     * @param  tenantModuleVo 테넌트모듈정보 TenantModuleVo
     * @return 번호
     * @throws Exception
     */
	public int deleteTenantModule(TenantModuleVo tenantModuleVo) throws Exception;
	
    /**
     * 테넌트가 보유한 모듈 상세 정보를 조회한다.
     *
     * @param  tenantId 테넌트 ID
     * @return 테넌트가 보유한 모듈 상세 정보 목록
     * @throws Exception
     */
	public List<TenantModuleDetailVo> selectTenantModuleDetails(String tenantId) throws Exception;
	
}
