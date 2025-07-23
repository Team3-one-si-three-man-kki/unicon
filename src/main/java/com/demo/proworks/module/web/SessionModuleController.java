package com.demo.proworks.module.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.demo.proworks.module.service.ModuleService;
import com.demo.proworks.module.service.SessionModuleService;
import com.demo.proworks.module.vo.SessionModuleVo;
import com.demo.proworks.session.vo.SessionVo;
import com.demo.proworks.module.vo.ModuleVo;
import com.demo.proworks.module.vo.SessionModuleDetailListVo;
import com.demo.proworks.module.vo.SessionModuleDetailVo;
import com.demo.proworks.module.vo.SessionModuleListVo;
import com.demo.proworks.module.vo.SessionModuleRequestVo;
import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

	@Resource(name = "moduleServiceImpl")
	private ModuleService moduleService;

	/**
	 * 세션모듈설정정보 목록을 조회합니다.
	 *
	 * @param sessionModuleVo 세션모듈설정정보
	 * @return 목록조회 결과
	 * @throws Exception
	 */
	@ElService(key = "TNU0002SessionModuleList")
	@RequestMapping(value = "TNU0002SessionModuleList")
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
	@ElService(key = "TNU0001SessionModuleUpdView")
	@RequestMapping(value = "TNU0001SessionModuleUpdView")
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
	@ElService(key = "TNU0001SessionModuleIns")
	@RequestMapping(value = "TNU0001SessionModuleIns")
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
	@ElService(key = "TNU0002SessionModuleUpd")
	@RequestMapping(value = "TNU0002SessionModuleUpd")
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
	@ElService(key = "TNU0002SessionModuleDel")
	@RequestMapping(value = "TNU0002SessionModuleDel")
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
	@ElService(key = "TNU0001SessionModuleListIns")
	@RequestMapping(value = "TNU0001SessionModuleListIns")
	@ElDescription(sub = "세션모듈설정정보 다건 등록처리", desc = "세션모듈설정정보를 다건 등록처리한다")
	public void insertSessionModuleList(SessionModuleListVo sessionModuleVoList) throws Exception {

		for (SessionModuleVo vo : sessionModuleVoList.getSessionModuleVoList()) {
			sessionModuleService.insertSessionModule(vo); // 하나씩 저장
		}
	}

	/**
	 * 특정 세션의 모듈 상세 목록을 조회합니다.
	 *
	 * @param sessionId 세션ID
	 * @return 해당 세션의 모듈 상세 목록
	 * @throws Exception
	 */
	@ElService(key = "TNU0001SessionModuleDetailListBySessionId")
	@RequestMapping(value = "TNU0001SessionModuleDetailListBySessionId")
	@ElDescription(sub = "세션별 모듈 상세 목록조회", desc = "특정 세션ID로 해당 세션의 모듈 상세 정보를 조회한다.")
	public SessionModuleDetailListVo selectListSessionModuleDetailBySessionId(SessionModuleRequestVo requestData)
			throws Exception {

		String sessionId = requestData.getSessionId();

		List<SessionModuleDetailVo> sessionModuleDetailList = sessionModuleService
				.selectListSessionModuleDetailBySessionId(sessionId);

		SessionModuleDetailListVo retSessionModuleDetailList = new SessionModuleDetailListVo();
		retSessionModuleDetailList.setSessionModuleDetailVoList(sessionModuleDetailList);
		retSessionModuleDetailList.setTotalCount((long) sessionModuleDetailList.size());

		// >>>>현재 로그인한 유저의 테넌트 아이디 뽑아내기
//		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
//				.getRequest();
//		String tenantId = (String) request.getAttribute("tenantId");
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tenantId);

		return retSessionModuleDetailList;
	}

	@ElService(key = "TNU0002ML")
	@RequestMapping(value = "TNU0002ML")
	@ElDescription(sub = "모듈별 세션리스트조회", desc = "모듈ID로 세션리스트를 조회한다")
	public List<SessionVo> getSessionByModule(ModuleVo module) throws Exception {
		System.out.println("모듈별세션리스트조회 컨트롤러");
		List<SessionVo> module1 = new ArrayList<SessionVo>();
		module1=moduleService.getSessionsByModule(module.getModuleId());
		System.out.println("가져온 데이터는"+module1);
		return module1;
	}

}
