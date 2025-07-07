package com.demo.proworks.session.service;

import java.util.List;

import com.demo.proworks.session.vo.SessionVo;

/**  
 * @subject     : 세션정보 관련 처리를 담당하는 인터페이스
 * @description : 세션정보 관련 처리를 담당하는 인터페이스
 * @author      : 여경원
 * @since       : 2025/07/04
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/04			 여경원	 		최초 생성
 * 
 */
public interface SessionService {
	
    /**
     * 세션정보 페이징 처리하여 목록을 조회한다.
     *
     * @param  sessionVo 세션정보 SessionVo
     * @return 세션정보 목록 List<SessionVo>
     * @throws Exception
     */
	public List<SessionVo> selectListSession(SessionVo sessionVo) throws Exception;
	
    /**
     * 조회한 세션정보 전체 카운트
     * 
     * @param  sessionVo 세션정보 SessionVo
     * @return 세션정보 목록 전체 카운트
     * @throws Exception
     */
	public long selectListCountSession(SessionVo sessionVo) throws Exception;
	
    /**
     * 세션정보를 상세 조회한다.
     *
     * @param  sessionVo 세션정보 SessionVo
     * @return 단건 조회 결과
     * @throws Exception
     */
	public SessionVo selectSession(SessionVo sessionVo) throws Exception;
		
    /**
     * 세션정보를 등록 처리 한다.
     *
     * @param  sessionVo 세션정보 SessionVo
     * @return 번호
     * @throws Exception
     */
	public int insertSession(SessionVo sessionVo) throws Exception;
	
    /**
     * 세션정보를 갱신 처리 한다.
     *
     * @param  sessionVo 세션정보 SessionVo
     * @return 번호
     * @throws Exception
     */
	public int updateSession(SessionVo sessionVo) throws Exception;
	
    /**
     * 세션정보를 삭제 처리 한다.
     *
     * @param  sessionVo 세션정보 SessionVo
     * @return 번호
     * @throws Exception
     */
	public int deleteSession(SessionVo sessionVo) throws Exception;
	
}
