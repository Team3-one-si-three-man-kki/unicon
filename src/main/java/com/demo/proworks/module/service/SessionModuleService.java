package com.demo.proworks.module.service;

import java.util.List;

import com.demo.proworks.module.vo.SessionModuleDetailVo;
import com.demo.proworks.module.vo.SessionModuleVo;

/**
 * @subject : 세션모듈설정정보 관련 처리를 담당하는 인터페이스
 * @description : 세션모듈설정정보 관련 처리를 담당하는 인터페이스
 * @author : 여경원
 * @since : 2025/07/02
 * @modification ===========================================================
 *               DATE AUTHOR DESC
 *               ===========================================================
 *               2025/07/02 여경원 최초 생성
 * 
 */
public interface SessionModuleService {

	/**
	 * 세션모듈설정정보 페이징 처리하여 목록을 조회한다.
	 *
	 * @param sessionModuleVo 세션모듈설정정보 SessionModuleVo
	 * @return 세션모듈설정정보 목록 List<SessionModuleVo>
	 * @throws Exception
	 */
	public List<SessionModuleVo> selectListSessionModule(SessionModuleVo sessionModuleVo) throws Exception;

	/**
	 * 조회한 세션모듈설정정보 전체 카운트
	 * 
	 * @param sessionModuleVo 세션모듈설정정보 SessionModuleVo
	 * @return 세션모듈설정정보 목록 전체 카운트
	 * @throws Exception
	 */
	public long selectListCountSessionModule(SessionModuleVo sessionModuleVo) throws Exception;

	/**
	 * 세션모듈설정정보를 상세 조회한다.
	 *
	 * @param sessionModuleVo 세션모듈설정정보 SessionModuleVo
	 * @return 단건 조회 결과
	 * @throws Exception
	 */
	public SessionModuleVo selectSessionModule(SessionModuleVo sessionModuleVo) throws Exception;

	/**
	 * 세션모듈설정정보를 등록 처리 한다.
	 *
	 * @param sessionModuleVo 세션모듈설정정보 SessionModuleVo
	 * @return 번호
	 * @throws Exception
	 */
	public int insertSessionModule(SessionModuleVo sessionModuleVo) throws Exception;

	/**
	 * 세션모듈설정정보를 갱신 처리 한다.
	 *
	 * @param sessionModuleVo 세션모듈설정정보 SessionModuleVo
	 * @return 번호
	 * @throws Exception
	 */
	public int updateSessionModule(SessionModuleVo sessionModuleVo) throws Exception;

	/**
	 * 세션모듈설정정보를 삭제 처리 한다.
	 *
	 * @param sessionModuleVo 세션모듈설정정보 SessionModuleVo
	 * @return 번호
	 * @throws Exception
	 */
	public int deleteSessionModule(SessionModuleVo sessionModuleVo) throws Exception;

	/**
	 * 특정 세션의 모듈 상세 목록을 조회한다.
	 *
	 * @param sessionId 세션ID
	 * @return 해당 세션의 모듈 상세 목록 List<SessionModuleDetailVo>
	 * @throws Exception
	 */
	public List<SessionModuleDetailVo> selectListSessionModuleDetailBySessionId(String sessionId) throws Exception;
}
