package com.demo.proworks.module.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.demo.proworks.module.service.SessionModuleService;
import com.demo.proworks.module.vo.SessionModuleVo;
import com.demo.proworks.module.dao.SessionModuleDAO;

/**  
 * @subject     : 세션모듈설정정보 관련 처리를 담당하는 ServiceImpl
 * @description	: 세션모듈설정정보 관련 처리를 담당하는 ServiceImpl
 * @author      : 여경원
 * @since       : 2025/07/02
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/02			 여경원	 		최초 생성
 * 
 */
@Service("sessionModuleServiceImpl")
public class SessionModuleServiceImpl implements SessionModuleService {

    @Resource(name="sessionModuleDAO")
    private SessionModuleDAO sessionModuleDAO;
	
	@Resource(name = "messageSource")
	private MessageSource messageSource;

    /**
     * 세션모듈설정정보 목록을 조회합니다.
     *
     * @process
     * 1. 세션모듈설정정보 페이징 처리하여 목록을 조회한다.
     * 2. 결과 List<SessionModuleVo>을(를) 리턴한다.
     * 
     * @param  sessionModuleVo 세션모듈설정정보 SessionModuleVo
     * @return 세션모듈설정정보 목록 List<SessionModuleVo>
     * @throws Exception
     */
	public List<SessionModuleVo> selectListSessionModule(SessionModuleVo sessionModuleVo) throws Exception {
		List<SessionModuleVo> list = sessionModuleDAO.selectListSessionModule(sessionModuleVo);	
	
		return list;
	}

    /**
     * 조회한 세션모듈설정정보 전체 카운트
     *
     * @process
     * 1. 세션모듈설정정보 조회하여 전체 카운트를 리턴한다.
     * 
     * @param  sessionModuleVo 세션모듈설정정보 SessionModuleVo
     * @return 세션모듈설정정보 목록 전체 카운트
     * @throws Exception
     */
	public long selectListCountSessionModule(SessionModuleVo sessionModuleVo) throws Exception {
		return sessionModuleDAO.selectListCountSessionModule(sessionModuleVo);
	}

    /**
     * 세션모듈설정정보를 상세 조회한다.
     *
     * @process
     * 1. 세션모듈설정정보를 상세 조회한다.
     * 2. 결과 SessionModuleVo을(를) 리턴한다.
     * 
     * @param  sessionModuleVo 세션모듈설정정보 SessionModuleVo
     * @return 단건 조회 결과
     * @throws Exception
     */
	public SessionModuleVo selectSessionModule(SessionModuleVo sessionModuleVo) throws Exception {
		SessionModuleVo resultVO = sessionModuleDAO.selectSessionModule(sessionModuleVo);			
        
        return resultVO;
	}

    /**
     * 세션모듈설정정보를 등록 처리 한다.
     *
     * @process
     * 1. 세션모듈설정정보를 등록 처리 한다.
     * 
     * @param  sessionModuleVo 세션모듈설정정보 SessionModuleVo
     * @return 번호
     * @throws Exception
     */
	public int insertSessionModule(SessionModuleVo sessionModuleVo) throws Exception {
		return sessionModuleDAO.insertSessionModule(sessionModuleVo);	
	}
	
    /**
     * 세션모듈설정정보를 갱신 처리 한다.
     *
     * @process
     * 1. 세션모듈설정정보를 갱신 처리 한다.
     * 
     * @param  sessionModuleVo 세션모듈설정정보 SessionModuleVo
     * @return 번호
     * @throws Exception
     */
	public int updateSessionModule(SessionModuleVo sessionModuleVo) throws Exception {				
		return sessionModuleDAO.updateSessionModule(sessionModuleVo);	   		
	}

    /**
     * 세션모듈설정정보를 삭제 처리 한다.
     *
     * @process
     * 1. 세션모듈설정정보를 삭제 처리 한다.
     * 
     * @param  sessionModuleVo 세션모듈설정정보 SessionModuleVo
     * @return 번호
     * @throws Exception
     */
	public int deleteSessionModule(SessionModuleVo sessionModuleVo) throws Exception {
		return sessionModuleDAO.deleteSessionModule(sessionModuleVo);
	}
	
}
