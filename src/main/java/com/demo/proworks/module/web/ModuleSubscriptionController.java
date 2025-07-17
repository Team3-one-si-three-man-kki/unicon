package com.demo.proworks.module.web;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.proworks.module.service.ModuleService;
import com.demo.proworks.module.vo.TenantModuleVo;
import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;

/**
 * @subject : 모듈 구독 관련 처리를 담당하는 컨트롤러
 * @description : 테넌트의 모듈 구독/해지 처리
 * @author : KIM SI YEON
 * @since : 2025/07/12
 */
@Controller
public class ModuleSubscriptionController {
	@Resource
	private ModuleService moduleService;

	/**
	 * 모듈 구독 처리
	 *
	 * @param tenantModuleVo 테넌트모듈 정보
	 * @throws Exception
	 */
	@ElService(key = "TMD0001Subscribe")
	@RequestMapping(value = "TMD0001Subscribe")
	@ElDescription(sub = "모듈구독처리", desc = "테넌트가 모듈을 구독 처리한다.")
	public void subscribeModule(TenantModuleVo tenantModuleVo) throws Exception {

		// 임시로 tenantId를 1로 설정 (추후 로그인 사용자의 tenantId로 변경)
		if (tenantModuleVo.getTenantId() == null || tenantModuleVo.getTenantId().isEmpty()) {
			tenantModuleVo.setTenantId("1");
		}

		// 모듈 구독 처리
		moduleService.subscribeModule(tenantModuleVo);
	}

	/**
	 * 모듈 구독 해지 처리
	 *
	 * @param tenantModuleVo 테넌트모듈 정보
	 * @throws Exception
	 */
	@ElService(key = "TMD0001Unsubscribe")
	@RequestMapping(value = "TMD0001Unsubscribe")
	@ElDescription(sub = "모듈구독해지처리", desc = "테넌트가 모듈 구독을 해지 처리한다.")
	public void unsubscribeModule(TenantModuleVo tenantModuleVo) throws Exception {

		// 임시로 tenantId를 1로 설정 (추후 로그인 사용자의 tenantId로 변경)
		if (tenantModuleVo.getTenantId() == null || tenantModuleVo.getTenantId().isEmpty()) {
			tenantModuleVo.setTenantId("1");
		}

		// 모듈 구독 해지 처리
		moduleService.unsubscribeModule(tenantModuleVo);
	}

	/**
	 * 테넌트의 구독 모듈 목록 조회
	 *
	 * @param tenantModuleVo 테넌트모듈 정보
	 * @return 구독 모듈 목록
	 * @throws Exception
	 */
	@ElService(key = "TMD0002List")
	@RequestMapping(value = "TMD0002List")
	@ElDescription(sub = "구독모듈목록조회", desc = "테넌트가 구독중인 모듈 목록을 조회한다.")
	public java.util.List<TenantModuleVo> selectSubscribedModules(TenantModuleVo tenantModuleVo) throws Exception {

		// 임시로 tenantId를 1로 설정 (추후 로그인 사용자의 tenantId로 변경)
		if (tenantModuleVo.getTenantId() == null || tenantModuleVo.getTenantId().isEmpty()) {
			tenantModuleVo.setTenantId("1");
		}

		return moduleService.selectSubscribedModules(tenantModuleVo);
	}

	/**
	 * 모듈 구독 상태 확인
	 *
	 * @param tenantModuleVo 테넌트모듈 정보
	 * @return 구독 여부 (true: 구독중, false: 미구독)
	 * @throws Exception
	 */
	@ElService(key = "TMD0001CheckSubscription")
	@RequestMapping(value = "TMD0001CheckSubscription")
	@ElDescription(sub = "모듈구독상태확인", desc = "특정 모듈의 구독 상태를 확인한다.")
	public boolean checkModuleSubscription(TenantModuleVo tenantModuleVo) throws Exception {

		// 임시로 tenantId를 1로 설정 (추후 로그인 사용자의 tenantId로 변경)
		if (tenantModuleVo.getTenantId() == null || tenantModuleVo.getTenantId().isEmpty()) {
			tenantModuleVo.setTenantId("1");
		}

		return moduleService.isModuleSubscribed(tenantModuleVo);
	}
}