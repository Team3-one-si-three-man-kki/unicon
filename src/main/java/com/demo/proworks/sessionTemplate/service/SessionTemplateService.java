package com.demo.proworks.sessionTemplate.service;

import java.util.List;

import com.demo.proworks.sessionTemplate.vo.SessionTemplateVo;

/**  
 * @subject     : 세션별 화면 레이아웃 정보 관련 처리를 담당하는 인터페이스
 * @description : 세션별 화면 레이아웃 정보 관련 처리를 담당하는 인터페이스
 * @author      : 여경원
 * @since       : 2025/07/03
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/03			 여경원	 		최초 생성
 * 
 */
public interface SessionTemplateService {
	
    /**
     * 세션별 화면 레이아웃 정보 페이징 처리하여 목록을 조회한다.
     *
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보 SessionTemplateVo
     * @return 세션별 화면 레이아웃 정보 목록 List<SessionTemplateVo>
     * @throws Exception
     */
	public List<SessionTemplateVo> selectListSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception;
	
    /**
     * 조회한 세션별 화면 레이아웃 정보 전체 카운트
     * 
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보 SessionTemplateVo
     * @return 세션별 화면 레이아웃 정보 목록 전체 카운트
     * @throws Exception
     */
	public long selectListCountSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception;
	
    /**
     * 세션별 화면 레이아웃 정보를 상세 조회한다.
     *
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보 SessionTemplateVo
     * @return 단건 조회 결과
     * @throws Exception
     */
	public SessionTemplateVo selectSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception;
		
    /**
     * 세션별 화면 레이아웃 정보를 등록 처리 한다.
     *
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보 SessionTemplateVo
     * @return 번호
     * @throws Exception
     */
	public int insertSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception;
	
    /**
     * 세션별 화면 레이아웃 정보를 갱신 처리 한다.
     *
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보 SessionTemplateVo
     * @return 번호
     * @throws Exception
     */
	public int updateSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception;
	
    /**
     * 세션별 화면 레이아웃 정보를 삭제 처리 한다.
     *
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보 SessionTemplateVo
     * @return 번호
     * @throws Exception
     */
	public int deleteSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception;
	
}
