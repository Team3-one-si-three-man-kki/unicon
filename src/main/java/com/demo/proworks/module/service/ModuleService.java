package com.demo.proworks.module.service;

import java.util.List;

import com.demo.proworks.module.vo.ModuleVo;
import com.demo.proworks.module.vo.TenantModuleVo;
import com.demo.proworks.session.vo.SessionVo;

/**
 * @subject : 모듈정보 관련 처리를 담당하는 인터페이스
 * @description : 모듈정보 관련 처리를 담당하는 인터페이스
 * @author : 여경원
 * @since : 2025/07/01
 * @modification ===========================================================
 *               DATE AUTHOR DESC
 *               ===========================================================
 *               2025/07/01 여경원 최초 생성
 * 
 */
public interface ModuleService {

	/**
	 * 모듈정보 페이징 처리하여 목록을 조회한다.
	 *
	 * @param moduleVo 모듈정보 ModuleVo
	 * @return 모듈정보 목록 List<ModuleVo>
	 * @throws Exception
	 */
	public List<ModuleVo> selectListModule(ModuleVo moduleVo) throws Exception;

	/**
	 * 조회한 모듈정보 전체 카운트
	 * 
	 * @param moduleVo 모듈정보 ModuleVo
	 * @return 모듈정보 목록 전체 카운트
	 * @throws Exception
	 */
	public long selectListCountModule(ModuleVo moduleVo) throws Exception;

	/**
	 * 모듈정보를 상세 조회한다.
	 *
	 * @param moduleVo 모듈정보 ModuleVo
	 * @return 단건 조회 결과
	 * @throws Exception
	 */
	public ModuleVo selectModule(ModuleVo moduleVo) throws Exception;

	/**
	 * 모듈정보를 등록 처리 한다.
	 *
	 * @param moduleVo 모듈정보 ModuleVo
	 * @return 번호
	 * @throws Exception
	 */
	public int insertModule(ModuleVo moduleVo) throws Exception;

	/**
	 * 모듈정보를 갱신 처리 한다.
	 *
	 * @param moduleVo 모듈정보 ModuleVo
	 * @return 번호
	 * @throws Exception
	 */
	public int updateModule(ModuleVo moduleVo) throws Exception;

	/**
	 * 모듈정보를 삭제 처리 한다.
	 *
	 * @param moduleVo 모듈정보 ModuleVo
	 * @return 번호
	 * @throws Exception
	 */
	public int deleteModule(ModuleVo moduleVo) throws Exception;



	/**
	 * 모듈 구독 처리
	 *
	 * @param tenantModuleVo 테넌트모듈 정보
	 * @return 처리 결과 (성공: 1, 실패: 0)
	 * @throws Exception
	 */
	public int subscribeModule(TenantModuleVo tenantModuleVo) throws Exception;

	/**
	 * 모듈 구독 해지 처리
	 *
	 * @param tenantModuleVo 테넌트모듈 정보
	 * @return 처리 결과 (성공: 1, 실패: 0)
	 * @throws Exception
	 */
	public int unsubscribeModule(TenantModuleVo tenantModuleVo) throws Exception;

	/**
	 * 테넌트의 구독 모듈 목록 조회
	 *
	 * @param tenantModuleVo 테넌트모듈 정보 (tenantId 포함)
	 * @return 구독 모듈 목록
	 * @throws Exception
	 */
	public List<TenantModuleVo> selectSubscribedModules(TenantModuleVo tenantModuleVo) throws Exception;

	/**
	 * 모듈 구독 상태 확인
	 *
	 * @param tenantModuleVo 테넌트모듈 정보 (moduleId, tenantId 포함)
	 * @return 구독 여부 (true: 구독중, false: 미구독)
	 * @throws Exception
	 */
	public boolean isModuleSubscribed(TenantModuleVo tenantModuleVo) throws Exception;

	/**
	 * 중복 구독 체크
	 *
	 * @param tenantModuleVo 테넌트모듈 정보
	 * @return 중복 여부 (true: 중복, false: 중복 아님)
	 * @throws Exception
	 */
	public boolean isDuplicateSubscription(TenantModuleVo tenantModuleVo) throws Exception;
	
	public List<SessionVo> getSessionsByModule(String moduleId) throws Exception;

}
