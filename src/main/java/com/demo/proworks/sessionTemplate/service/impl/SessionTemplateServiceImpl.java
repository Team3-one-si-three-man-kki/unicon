package com.demo.proworks.sessionTemplate.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.demo.proworks.sessionTemplate.service.SessionTemplateService;
import com.demo.proworks.sessionTemplate.vo.SessionTemplateVo;
import com.demo.proworks.sessionTemplate.dao.SessionTemplateDAO;

/**  
 * @subject     : 세션별 화면 레이아웃 정보 관련 처리를 담당하는 ServiceImpl
 * @description	: 세션별 화면 레이아웃 정보 관련 처리를 담당하는 ServiceImpl
 * @author      : 여경원
 * @since       : 2025/07/03
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/03			 여경원	 		최초 생성
 * 
 */
@Service("sessionTemplateServiceImpl")
public class SessionTemplateServiceImpl implements SessionTemplateService {

    @Resource(name="sessionTemplateDAO")
    private SessionTemplateDAO sessionTemplateDAO;
	
	@Resource(name = "messageSource")
	private MessageSource messageSource;

    /**
     * 세션별 화면 레이아웃 정보 목록을 조회합니다.
     *
     * @process
     * 1. 세션별 화면 레이아웃 정보 페이징 처리하여 목록을 조회한다.
     * 2. 결과 List<SessionTemplateVo>을(를) 리턴한다.
     * 
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보 SessionTemplateVo
     * @return 세션별 화면 레이아웃 정보 목록 List<SessionTemplateVo>
     * @throws Exception
     */
	public List<SessionTemplateVo> selectListSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception {
		List<SessionTemplateVo> list = sessionTemplateDAO.selectListSessionTemplate(sessionTemplateVo);	
	
		return list;
	}

    /**
     * 조회한 세션별 화면 레이아웃 정보 전체 카운트
     *
     * @process
     * 1. 세션별 화면 레이아웃 정보 조회하여 전체 카운트를 리턴한다.
     * 
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보 SessionTemplateVo
     * @return 세션별 화면 레이아웃 정보 목록 전체 카운트
     * @throws Exception
     */
	public long selectListCountSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception {
		return sessionTemplateDAO.selectListCountSessionTemplate(sessionTemplateVo);
	}

    /**
     * 세션별 화면 레이아웃 정보를 상세 조회한다.
     *
     * @process
     * 1. 세션별 화면 레이아웃 정보를 상세 조회한다.
     * 2. 결과 SessionTemplateVo을(를) 리턴한다.
     * 
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보 SessionTemplateVo
     * @return 단건 조회 결과
     * @throws Exception
     */
	public SessionTemplateVo selectSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception {
		SessionTemplateVo resultVO = sessionTemplateDAO.selectSessionTemplate(sessionTemplateVo);			
        
        return resultVO;
	}

    /**
     * 세션별 화면 레이아웃 정보를 등록 처리 한다.
     *
     * @process
     * 1. 세션별 화면 레이아웃 정보를 등록 처리 한다.
     * 
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보 SessionTemplateVo
     * @return 번호
     * @throws Exception
     */
	public int insertSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception {
		return sessionTemplateDAO.insertSessionTemplate(sessionTemplateVo);	
	}
	
    /**
     * 세션별 화면 레이아웃 정보를 갱신 처리 한다.
     *
     * @process
     * 1. 세션별 화면 레이아웃 정보를 갱신 처리 한다.
     * 
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보 SessionTemplateVo
     * @return 번호
     * @throws Exception
     */
	public int updateSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception {				
		return sessionTemplateDAO.updateSessionTemplate(sessionTemplateVo);	   		
	}

    /**
     * 세션별 화면 레이아웃 정보를 삭제 처리 한다.
     *
     * @process
     * 1. 세션별 화면 레이아웃 정보를 삭제 처리 한다.
     * 
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보 SessionTemplateVo
     * @return 번호
     * @throws Exception
     */
	public int deleteSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception {
		return sessionTemplateDAO.deleteSessionTemplate(sessionTemplateVo);
	}
	
}
