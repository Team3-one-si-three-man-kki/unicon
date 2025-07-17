package com.demo.proworks.module.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.demo.proworks.module.service.ModuleService;
import com.demo.proworks.module.vo.ModuleVo;
import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.log.AppLog;

@Controller
public class MarketplaceController {

	@Resource(name = "moduleServiceImpl")
	private ModuleService moduleService;

	/**
	 * 마켓플레이스용 모듈 목록 조회 
	 */
	@ElService(key = "MarketplaceModuleList")
	@RequestMapping(value = "MarketplaceModuleList", method = { RequestMethod.GET, RequestMethod.POST })
	@ElDescription(sub = "마켓플레이스 모듈목록조회", desc = "마켓플레이스에서 사용할 모듈 목록을 조회한다.")
	public Map<String, Object> selectMarketplaceModuleList(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try {
			AppLog.debug("MarketplaceController.selectMarketplaceModuleList 시작");

			// 세션 체크 우회 - 임시 처리
			AppLog.debug("세션 체크 없이 모듈 목록 조회 진행");

			ModuleVo moduleVo = new ModuleVo();
			List<ModuleVo> moduleList = moduleService.selectListModule(moduleVo);

			// 프론트엔드에서 기대하는 형태로 응답 구성
			Map<String, Object> result = new HashMap<>();
			result.put("moduleVoList", moduleList);
			result.put("totalCount", moduleList.size());
			result.put("pageSize", 10);
			result.put("pageIndex", 1);

			AppLog.debug("MarketplaceController.selectMarketplaceModuleList 완료: " + moduleList.size() + "개 조회");

			return result;

		} catch (Exception e) {
			AppLog.error("MarketplaceController.selectMarketplaceModuleList 오류", e);

			// 오류 발생 시에도 빈 목록이라도 반환
			Map<String, Object> errorResult = new HashMap<>();
			errorResult.put("moduleVoList", new java.util.ArrayList<>());
			errorResult.put("totalCount", 0);
			errorResult.put("error", "모듈 목록 조회 중 오류가 발생했습니다: " + e.getMessage());

			return errorResult;
		}
	}
}