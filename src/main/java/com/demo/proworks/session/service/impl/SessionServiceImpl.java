package com.demo.proworks.session.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.demo.proworks.session.service.SessionService;
import com.demo.proworks.session.vo.SessionVo;
import com.demo.proworks.session.dao.SessionDAO;

/**  
 * @subject     : 세션정보 관련 처리를 담당하는 ServiceImpl
 * @description	: 세션정보 관련 처리를 담당하는 ServiceImpl
 * @author      : 여경원
 * @since       : 2025/07/04
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/04			 여경원	 		최초 생성
 * 
 */
@Service("sessionServiceImpl")
public class SessionServiceImpl implements SessionService {

    @Resource(name="sessionDAO")
    private SessionDAO sessionDAO;
	
	@Resource(name = "messageSource")
	private MessageSource messageSource;

    /**
     * 세션정보 목록을 조회합니다.
     *
     * @process
     * 1. 세션정보 페이징 처리하여 목록을 조회한다.
     * 2. 결과 List<SessionVo>을(를) 리턴한다.
     * 
     * @param  sessionVo 세션정보 SessionVo
     * @return 세션정보 목록 List<SessionVo>
     * @throws Exception
     */
	public List<SessionVo> selectListSession(SessionVo sessionVo) throws Exception {
		List<SessionVo> list = sessionDAO.selectListSession(sessionVo);	
	
		return list;
	}

    /**
     * 조회한 세션정보 전체 카운트
     *
     * @process
     * 1. 세션정보 조회하여 전체 카운트를 리턴한다.
     * 
     * @param  sessionVo 세션정보 SessionVo
     * @return 세션정보 목록 전체 카운트
     * @throws Exception
     */
	public long selectListCountSession(SessionVo sessionVo) throws Exception {
		return sessionDAO.selectListCountSession(sessionVo);
	}

    /**
     * 세션정보를 상세 조회한다.
     *
     * @process
     * 1. 세션정보를 상세 조회한다.
     * 2. 결과 SessionVo을(를) 리턴한다.
     * 
     * @param  sessionVo 세션정보 SessionVo
     * @return 단건 조회 결과
     * @throws Exception
     */
	public SessionVo selectSession(SessionVo sessionVo) throws Exception {
		SessionVo resultVO = sessionDAO.selectSession(sessionVo);			
        
        return resultVO;
	}

    /**
     * 세션정보를 등록 처리 한다.
     *
     * @process
     * 1. 세션정보를 등록 처리 한다.
     * 
     * @param  sessionVo 세션정보 SessionVo
     * @return 번호
     * @throws Exception
     */
	public int insertSession(SessionVo sessionVo) throws Exception {
		return sessionDAO.insertSession(sessionVo);	
	}
	
    /**
     * 세션정보를 갱신 처리 한다.
     *
     * @process
     * 1. 세션정보를 갱신 처리 한다.
     * 
     * @param  sessionVo 세션정보 SessionVo
     * @return 번호
     * @throws Exception
     */
	public int updateSession(SessionVo sessionVo) throws Exception {				
		return sessionDAO.updateSession(sessionVo);	   		
	}

    /**
     * 세션정보를 삭제 처리 한다.
     *
     * @process
     * 1. 세션정보를 삭제 처리 한다.
     * 
     * @param  sessionVo 세션정보 SessionVo
     * @return 번호
     * @throws Exception
     */
	public int deleteSession(SessionVo sessionVo) throws Exception {
		return sessionDAO.deleteSession(sessionVo);
	}
	
}
