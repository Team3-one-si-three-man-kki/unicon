package com.demo.proworks.session.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.proworks.session.service.SessionService;
import com.demo.proworks.session.vo.SessionVo;
import com.demo.proworks.session.vo.SessionListVo;

import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.annotation.ElValidator;

/**
 * @subject : 세션정보 관련 처리를 담당하는 컨트롤러
 * @description : 세션정보 관련 처리를 담당하는 컨트롤러
 * @author : 여경원
 * @since : 2025/07/04
 * @modification ===========================================================
 *               DATE AUTHOR DESC
 *               ===========================================================
 *               2025/07/04 여경원 최초 생성
 * 
 */
@Controller
public class SessionController {

	/** SessionService */
	@Resource(name = "sessionServiceImpl")
	private SessionService sessionService;

	/**
	 * 세션정보 목록을 조회합니다.
	 *
	 * @param sessionVo 세션정보
	 * @return 목록조회 결과
	 * @throws Exception
	 */
	@ElService(key = "TNU0002SessionList")
	@RequestMapping(value = "TNU0002SessionList")
	@ElDescription(sub = "세션정보 목록조회", desc = "페이징을 처리하여 세션정보 목록 조회를 한다.")
	public SessionListVo selectListSession(SessionVo sessionVo) throws Exception {

		List<SessionVo> sessionList = sessionService.selectListSession(sessionVo);
		long totCnt = sessionService.selectListCountSession(sessionVo);

		SessionListVo retSessionList = new SessionListVo();
		retSessionList.setSessionVoList(sessionList);
		retSessionList.setTotalCount(totCnt);
		retSessionList.setPageSize(sessionVo.getPageSize());
		retSessionList.setPageIndex(sessionVo.getPageIndex());

		return retSessionList;
	}

	/**
	 * 세션정보을 단건 조회 처리 한다.
	 *
	 * @param sessionVo 세션정보
	 * @return 단건 조회 결과
	 * @throws Exception
	 */
	@ElService(key = "TNU0002SessionUpdView")
	@RequestMapping(value = "TNU0002SessionUpdView")
	@ElDescription(sub = "세션정보 갱신 폼을 위한 조회", desc = "세션정보 갱신 폼을 위한 조회를 한다.")
	public SessionVo selectSession(SessionVo sessionVo) throws Exception {
		SessionVo selectSessionVo = sessionService.selectSession(sessionVo);

		return selectSessionVo;
	}

	/**
	 * 세션정보를 등록 처리 한다.
	 *
	 * @param sessionVo 세션정보
	 * @throws Exception
	 */
	@ElService(key = "TNU0002SessionIns")
	@RequestMapping(value = "TNU0002SessionIns")
	@ElDescription(sub = "세션정보 등록처리", desc = "세션정보를 등록 처리 한다.")
	public Map<String, Object> insertSession(SessionVo sessionVo) throws Exception {
		// 세션 저장 후 생성된 ID 받기
		sessionService.insertSession(sessionVo);

		// 생성된 sessionId 가져오기 (MyBatis에서 자동 설정됨)
		String sessionId = sessionVo.getSessionId();

		// 응답 데이터 구성
		Map<String, Object> response = new HashMap<>();
		response.put("sessionId", sessionId);
		response.put("success", true);
		response.put("message", "세션이 성공적으로 생성되었습니다.");

		return response;
	}

	/**
	 * 세션정보를 갱신 처리 한다.
	 *
	 * @param sessionVo 세션정보
	 * @throws Exception
	 */
	@ElService(key = "TNU0002SessionUpd")
	@RequestMapping(value = "TNU0002SessionUpd")
	@ElValidator(errUrl = "/session/sessionRegister", errContinue = true)
	@ElDescription(sub = "세션정보 갱신처리", desc = "세션정보를 갱신 처리 한다.")
	public void updateSession(SessionVo sessionVo) throws Exception {

		sessionService.updateSession(sessionVo);
	}

	/**
	 * 세션정보를 삭제 처리한다.
	 *
	 * @param sessionVo 세션정보
	 * @throws Exception
	 */
	@ElService(key = "TNU0002SessionDel")
	@RequestMapping(value = "TNU0002SessionDel")
	@ElDescription(sub = "세션정보 삭제처리", desc = "세션정보를 삭제 처리한다.")
	public void deleteSession(SessionVo sessionVo) throws Exception {
		sessionService.deleteSession(sessionVo);
	}

}
