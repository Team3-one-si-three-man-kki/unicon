package com.demo.proworks.module.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.demo.proworks.module.dao.ModuleDAO;
import com.demo.proworks.module.service.ModuleService;
import com.demo.proworks.module.vo.ModuleVo;
import com.demo.proworks.module.vo.TenantModuleVo;
import com.inswave.elfw.exception.ElException;
import com.inswave.elfw.log.AppLog;

/**
 * @subject : 모듈정보 관련 처리를 담당하는 ServiceImpl
 * @description : 모듈정보 관련 처리를 담당하는 ServiceImpl
 * @author : 여경원
 * @since : 2025/07/01
 * @modification ===========================================================
 *               DATE AUTHOR DESC
 *               ===========================================================
 *               2025/07/01 여경원 최초 생성
 * 
 */
@Service("moduleServiceImpl")
public class ModuleServiceImpl implements ModuleService {

	@Resource(name = "moduleDAO")
	private ModuleDAO moduleDAO;

	@Resource(name = "messageSource")
	private MessageSource messageSource;

	/**
	 * 모듈정보 목록을 조회합니다.
	 *
	 * @process 1. 모듈정보 페이징 처리하여 목록을 조회한다. 2. 결과 List<ModuleVo>을(를) 리턴한다.
	 * 
	 * @param moduleVo 모듈정보 ModuleVo
	 * @return 모듈정보 목록 List<ModuleVo>
	 * @throws Exception
	 */
	public List<ModuleVo> selectListModule(ModuleVo moduleVo) throws Exception {
		List<ModuleVo> list = moduleDAO.selectListModule(moduleVo);

		return list;
	}

	/**
	 * 조회한 모듈정보 전체 카운트
	 *
	 * @process 1. 모듈정보 조회하여 전체 카운트를 리턴한다.
	 * 
	 * @param moduleVo 모듈정보 ModuleVo
	 * @return 모듈정보 목록 전체 카운트
	 * @throws Exception
	 */
	public long selectListCountModule(ModuleVo moduleVo) throws Exception {
		return moduleDAO.selectListCountModule(moduleVo);
	}

	/**
	 * 모듈정보를 상세 조회한다.
	 *
	 * @process 1. 모듈정보를 상세 조회한다. 2. 결과 ModuleVo을(를) 리턴한다.
	 * 
	 * @param moduleVo 모듈정보 ModuleVo
	 * @return 단건 조회 결과
	 * @throws Exception
	 */
	public ModuleVo selectModule(ModuleVo moduleVo) throws Exception {
		ModuleVo resultVO = moduleDAO.selectModule(moduleVo);

		return resultVO;
	}

	/**
	 * 모듈정보를 등록 처리 한다.
	 *
	 * @process 1. 모듈정보를 등록 처리 한다.
	 * 
	 * @param moduleVo 모듈정보 ModuleVo
	 * @return 번호
	 * @throws Exception
	 */
	public int insertModule(ModuleVo moduleVo) throws Exception {
		return moduleDAO.insertModule(moduleVo);
	}

	/**
	 * 모듈정보를 갱신 처리 한다.
	 *
	 * @process 1. 모듈정보를 갱신 처리 한다.
	 * 
	 * @param moduleVo 모듈정보 ModuleVo
	 * @return 번호
	 * @throws Exception
	 */
	public int updateModule(ModuleVo moduleVo) throws Exception {
		return moduleDAO.updateModule(moduleVo);
	}

	/**
	 * 모듈정보를 삭제 처리 한다.
	 *
	 * @process 1. 모듈정보를 삭제 처리 한다.
	 * 
	 * @param moduleVo 모듈정보 ModuleVo
	 * @return 번호
	 * @throws Exception
	 */
	public int deleteModule(ModuleVo moduleVo) throws Exception {
		return moduleDAO.deleteModule(moduleVo);
	}


	/**
	 * 모듈 구독 처리
	 */
	@Override
	public int subscribeModule(TenantModuleVo tenantModuleVo) throws Exception {
		AppLog.debug("모듈 구독 처리 시작: " + tenantModuleVo.toString());

		try {

			if (tenantModuleVo.getModuleId() == null || tenantModuleVo.getModuleId().trim().isEmpty()) {
				throw new ElException("모듈 ID가 필요합니다.");
			}
			if (tenantModuleVo.getTenantId() == null || tenantModuleVo.getTenantId().trim().isEmpty()) {
				throw new ElException("테넌트 ID가 필요합니다.");
			}


			if (isDuplicateSubscription(tenantModuleVo)) {
				throw new ElException("이미 구독중인 모듈입니다.");
			}


			if (tenantModuleVo.getPurchasedAt() == null || tenantModuleVo.getPurchasedAt().trim().isEmpty()) {

				tenantModuleVo.setPurchasedAt(null);
			}


			int result = moduleDAO.insertTenantModule(tenantModuleVo);

			if (result > 0) {
				AppLog.debug("모듈 구독 성공: moduleId=" + tenantModuleVo.getModuleId() + ", tenantId="
						+ tenantModuleVo.getTenantId());
			} else {
				throw new ElException("모듈 구독 처리에 실패했습니다.");
			}

			return result;

		} catch (Exception e) {
			AppLog.error("모듈 구독 처리 중 오류 발생", e);
			throw e;
		}
	}

	/**
	 * 모듈 구독 해지 처리
	 */
	@Override
	public int unsubscribeModule(TenantModuleVo tenantModuleVo) throws Exception {
		AppLog.debug("모듈 구독 해지 처리 시작: " + tenantModuleVo.toString());

		try {

			if (tenantModuleVo.getModuleId() == null || tenantModuleVo.getModuleId().trim().isEmpty()) {
				throw new ElException("모듈 ID가 필요합니다.");
			}
			if (tenantModuleVo.getTenantId() == null || tenantModuleVo.getTenantId().trim().isEmpty()) {
				throw new ElException("테넌트 ID가 필요합니다.");
			}

			if (!isModuleSubscribed(tenantModuleVo)) {
				throw new ElException("구독하지 않은 모듈입니다.");
			}

			int result = moduleDAO.deleteTenantModule(tenantModuleVo);

			if (result > 0) {
				AppLog.debug("모듈 구독 해지 성공: moduleId=" + tenantModuleVo.getModuleId() + ", tenantId="
						+ tenantModuleVo.getTenantId());
			} else {
				throw new ElException("모듈 구독 해지 처리에 실패했습니다.");
			}

			return result;

		} catch (Exception e) {
			AppLog.error("모듈 구독 해지 처리 중 오류 발생", e);
			throw e;
		}
	}

	/**
	 * 테넌트의 구독 모듈 목록 조회
	 */
	@Override
	public List<TenantModuleVo> selectSubscribedModules(TenantModuleVo tenantModuleVo) throws Exception {
		AppLog.debug("구독 모듈 목록 조회: tenantId=" + tenantModuleVo.getTenantId());

		try {
			if (tenantModuleVo.getTenantId() == null || tenantModuleVo.getTenantId().trim().isEmpty()) {
				throw new ElException("테넌트 ID가 필요합니다.");
			}

			List<TenantModuleVo> result = moduleDAO.selectTenantModules(tenantModuleVo);
			AppLog.debug("구독 모듈 조회 결과: " + result.size() + "개");

			return result;

		} catch (Exception e) {
			AppLog.error("구독 모듈 목록 조회 중 오류 발생", e);
			throw e;
		}
	}

	/**
	 * 모듈 구독 상태 확인
	 */
	@Override
	public boolean isModuleSubscribed(TenantModuleVo tenantModuleVo) throws Exception {
		try {
			TenantModuleVo result = moduleDAO.selectTenantModule(tenantModuleVo);
			return result != null;
		} catch (Exception e) {
			AppLog.error("모듈 구독 상태 확인 중 오류 발생", e);
			throw e;
		}
	}

	/**
	 * 중복 구독 체크
	 */
	@Override
	public boolean isDuplicateSubscription(TenantModuleVo tenantModuleVo) throws Exception {
		return isModuleSubscribed(tenantModuleVo);
	}

}
