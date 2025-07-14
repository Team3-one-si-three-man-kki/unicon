package com.demo.proworks.module.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.inswave.elfw.exception.ElException;
import com.demo.proworks.module.vo.ModuleVo;
import com.demo.proworks.module.vo.TenantModuleVo;
import com.demo.proworks.module.dao.ModuleDAO;

/**
 * @subject : 모듈정보 관련 처리를 담당하는 DAO
 * @description : 모듈정보 관련 처리를 담당하는 DAO
 * @author : 여경원
 * @since : 2025/07/01
 * @modification ===========================================================
 *               DATE AUTHOR DESC
 *               ===========================================================
 *               2025/07/01 여경원 최초 생성
 * 
 */
@Repository("moduleDAO")
public class ModuleDAO extends com.demo.proworks.cmmn.dao.ProworksDefaultAbstractDAO {

	/**
	 * 모듈정보 상세 조회한다.
	 * 
	 * @param ModuleVo 모듈정보
	 * @return ModuleVo 모듈정보
	 * @throws ElException
	 */
	public ModuleVo selectModule(ModuleVo vo) throws ElException {
		return (ModuleVo) selectByPk("com.demo.proworks.module.selectModule", vo);
	}

	/**
	 * 페이징을 처리하여 모듈정보 목록조회를 한다.
	 * 
	 * @param ModuleVo 모듈정보
	 * @return List<ModuleVo> 모듈정보
	 * @throws ElException
	 */
	public List<ModuleVo> selectListModule(ModuleVo vo) throws ElException {
		return (List<ModuleVo>) list("com.demo.proworks.module.selectListModule", vo);
	}

	/**
	 * 모듈정보 목록 조회의 전체 카운트를 조회한다.
	 * 
	 * @param ModuleVo 모듈정보
	 * @return 모듈정보 조회의 전체 카운트
	 * @throws ElException
	 */
	public long selectListCountModule(ModuleVo vo) throws ElException {
		return (Long) selectByPk("com.demo.proworks.module.selectListCountModule", vo);
	}

	/**
	 * 모듈정보를 등록한다.
	 * 
	 * @param ModuleVo 모듈정보
	 * @return 번호
	 * @throws ElException
	 */
	public int insertModule(ModuleVo vo) throws ElException {
		return insert("com.demo.proworks.module.insertModule", vo);
	}

	/**
	 * 모듈정보를 갱신한다.
	 * 
	 * @param ModuleVo 모듈정보
	 * @return 번호
	 * @throws ElException
	 */
	public int updateModule(ModuleVo vo) throws ElException {
		return update("com.demo.proworks.module.updateModule", vo);
	}

	/**
	 * 모듈정보를 삭제한다.
	 * 
	 * @param ModuleVo 모듈정보
	 * @return 번호
	 * @throws ElException
	 */
	public int deleteModule(ModuleVo vo) throws ElException {
		return delete("com.demo.proworks.module.deleteModule", vo);
	}

	/**
	 * 테넌트 모듈 구독 정보 등록
	 *
	 * @param tenantModuleVo 테넌트모듈 정보
	 * @return 처리 결과
	 * @throws Exception
	 */
	public int insertTenantModule(TenantModuleVo tenantModuleVo) throws Exception {
		return insert("com.demo.proworks.module.insertTenantModule", tenantModuleVo);
	}

	/**
	 * 테넌트 모듈 구독 정보 삭제
	 *
	 * @param tenantModuleVo 테넌트모듈 정보
	 * @return 처리 결과
	 * @throws Exception
	 */
	public int deleteTenantModule(TenantModuleVo tenantModuleVo) throws Exception {
		return delete("com.demo.proworks.module.deleteTenantModule", tenantModuleVo);
	}

	/***
	 * 
	 * 테넌트의 구독 모듈 목록 조회**
	 * 
	 * @param tenantModuleVo 테넌트모듈 정보
	 * @return 구독 모듈 목록
	 * @throws ElException
	 */

	@SuppressWarnings("unchecked")
	public List<TenantModuleVo> selectTenantModules(TenantModuleVo tenantModuleVo) throws ElException {
		return (List<TenantModuleVo>) list("com.demo.proworks.module.selectTenantModules", tenantModuleVo);
	}

	/**
	 * 특정 테넌트-모듈 구독 정보 조회 (단건)
	 *
	 * @param tenantModuleVo 테넌트모듈 정보
	 * @return 구독 정보 (없으면 null)
	 * @throws ElException
	 */
	public TenantModuleVo selectTenantModule(TenantModuleVo tenantModuleVo) throws ElException {
		return (TenantModuleVo) selectByPk("com.demo.proworks.module.selectTenantModule", tenantModuleVo);
	}
}