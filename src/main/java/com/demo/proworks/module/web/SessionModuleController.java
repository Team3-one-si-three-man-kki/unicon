package com.demo.proworks.module.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.proworks.module.service.SessionModuleService;
import com.demo.proworks.module.vo.SessionModuleVo;
import com.demo.proworks.module.vo.SessionModuleListVo;

import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.annotation.ElValidator;

/**
 * @subject : 세션모듈설정정보 관련 처리를 담당하는 컨트롤러
 * @description : 세션모듈설정정보 관련 처리를 담당하는 컨트롤러
 * @author : 여경원
 * @since : 2025/07/02
 * @modification ===========================================================
 *               DATE AUTHOR DESC
 *               ===========================================================
 *               2025/07/02 여경원 최초 생성
 * 
 */
@Controller
public class SessionModuleController {

	/** SessionModuleService */
	@Resource(name = "sessionModuleServiceImpl")
	private SessionModuleService sessionModuleService;

	/**
	 * 세션모듈설정정보 목록을 조회합니다.
	 *
	 * @param sessionModuleVo 세션모듈설정정보
	 * @return 목록조회 결과
	 * @throws Exception
	 */
	@ElService(key = "SessionModuleList")
	@RequestMapping(value = "SessionModuleList")
	@ElDescription(sub = "세션모듈설정정보 목록조회", desc = "페이징을 처리하여 세션모듈설정정보 목록 조회를 한다.")
	public SessionModuleListVo selectListSessionModule(SessionModuleVo sessionModuleVo) throws Exception {

		List<SessionModuleVo> sessionModuleList = sessionModuleService.selectListSessionModule(sessionModuleVo);
		long totCnt = sessionModuleService.selectListCountSessionModule(sessionModuleVo);

		SessionModuleListVo retSessionModuleList = new SessionModuleListVo();
		retSessionModuleList.setSessionModuleVoList(sessionModuleList);
		retSessionModuleList.setTotalCount(totCnt);
		retSessionModuleList.setPageSize(sessionModuleVo.getPageSize());
		retSessionModuleList.setPageIndex(sessionModuleVo.getPageIndex());

		return retSessionModuleList;
	}

	/**
	 * 세션모듈설정정보을 단건 조회 처리 한다.
	 *
	 * @param sessionModuleVo 세션모듈설정정보
	 * @return 단건 조회 결과
	 * @throws Exception
	 */
	@ElService(key = "SessionModuleUpdView")
	@RequestMapping(value = "SessionModuleUpdView")
	@ElDescription(sub = "세션모듈설정정보 갱신 폼을 위한 조회", desc = "세션모듈설정정보 갱신 폼을 위한 조회를 한다.")
	public SessionModuleVo selectSessionModule(SessionModuleVo sessionModuleVo) throws Exception {
		SessionModuleVo selectSessionModuleVo = sessionModuleService.selectSessionModule(sessionModuleVo);

		return selectSessionModuleVo;
	}

	/**
	 * 세션모듈설정정보를 등록 처리 한다.
	 *
	 * @param sessionModuleVo 세션모듈설정정보
	 * @throws Exception
	 */
	@ElService(key = "SessionModuleIns")
	@RequestMapping(value = "SessionModuleIns")
	@ElDescription(sub = "세션모듈설정정보 등록처리", desc = "세션모듈설정정보를 등록 처리 한다.")
	public void insertSessionModule(SessionModuleVo sessionModuleVo) throws Exception {
		sessionModuleService.insertSessionModule(sessionModuleVo);
	}

	/**
	 * 세션모듈설정정보를 갱신 처리 한다.
	 *
	 * @param sessionModuleVo 세션모듈설정정보
	 * @throws Exception
	 */
	@ElService(key = "SessionModuleUpd")
	@RequestMapping(value = "SessionModuleUpd")
	@ElValidator(errUrl = "/sessionModule/sessionModuleRegister", errContinue = true)
	@ElDescription(sub = "세션모듈설정정보 갱신처리", desc = "세션모듈설정정보를 갱신 처리 한다.")
	public void updateSessionModule(SessionModuleVo sessionModuleVo) throws Exception {

		sessionModuleService.updateSessionModule(sessionModuleVo);
	}

	/**
	 * 세션모듈설정정보를 삭제 처리한다.
	 *
	 * @param sessionModuleVo 세션모듈설정정보
	 * @throws Exception
	 */
	@ElService(key = "SessionModuleDel")
	@RequestMapping(value = "SessionModuleDel")
	@ElDescription(sub = "세션모듈설정정보 삭제처리", desc = "세션모듈설정정보를 삭제 처리한다.")
	public void deleteSessionModule(SessionModuleVo sessionModuleVo) throws Exception {
		sessionModuleService.deleteSessionModule(sessionModuleVo);
	}

	/**
	 * 세션모듈설정정보를 다건 등록 처리 한다.
	 *
	 * @param sessionModuleListVo 세션모듈설정정보리스트
	 * @throws Exception
	 */
	@ElService(key = "SessionModuleListIns")
	@RequestMapping(value = "SessionModuleListIns")
	@ElDescription(sub = "세션모듈설정정보 다건 등록처리", desc = "세션모듈설정정보를 다건 등록처리한다")
	public void insertSessionModuleList(SessionModuleListVo sessionModuleVoList) throws Exception {

		for (SessionModuleVo vo : sessionModuleVoList.getSessionModuleVoList()) {
			sessionModuleService.insertSessionModule(vo); // 하나씩 저장
		}
	}
}
