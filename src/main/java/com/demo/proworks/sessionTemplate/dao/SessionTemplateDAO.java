package com.demo.proworks.sessionTemplate.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.inswave.elfw.exception.ElException;
import com.demo.proworks.sessionTemplate.vo.SessionTemplateVo;
import com.demo.proworks.sessionTemplate.dao.SessionTemplateDAO;

/**  
 * @subject     : 세션별 화면 레이아웃 정보 관련 처리를 담당하는 DAO
 * @description : 세션별 화면 레이아웃 정보 관련 처리를 담당하는 DAO
 * @author      : 여경원
 * @since       : 2025/07/03
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/03			 여경원	 		최초 생성
 * 
 */
@Repository("sessionTemplateDAO")
public class SessionTemplateDAO extends com.demo.proworks.cmmn.dao.ProworksDefaultAbstractDAO {

    /**
     * 세션별 화면 레이아웃 정보 상세 조회한다.
     *  
     * @param  SessionTemplateVo 세션별 화면 레이아웃 정보
     * @return SessionTemplateVo 세션별 화면 레이아웃 정보
     * @throws ElException
     */
    public SessionTemplateVo selectSessionTemplate(SessionTemplateVo vo) throws ElException {
        return (SessionTemplateVo) selectByPk("com.demo.proworks.sessionTemplate.selectSessionTemplate", vo);
    }

    /**
     * 페이징을 처리하여 세션별 화면 레이아웃 정보 목록조회를 한다.
     *  
     * @param  SessionTemplateVo 세션별 화면 레이아웃 정보
     * @return List<SessionTemplateVo> 세션별 화면 레이아웃 정보
     * @throws ElException
     */
    public List<SessionTemplateVo> selectListSessionTemplate(SessionTemplateVo vo) throws ElException {      	
        return (List<SessionTemplateVo>)list("com.demo.proworks.sessionTemplate.selectListSessionTemplate", vo);
    }

    /**
     * 세션별 화면 레이아웃 정보 목록 조회의 전체 카운트를 조회한다.
     *  
     * @param  SessionTemplateVo 세션별 화면 레이아웃 정보
     * @return 세션별 화면 레이아웃 정보 조회의 전체 카운트
     * @throws ElException
     */
    public long selectListCountSessionTemplate(SessionTemplateVo vo)  throws ElException{               
        return (Long)selectByPk("com.demo.proworks.sessionTemplate.selectListCountSessionTemplate", vo);
    }
        
    /**
     * 세션별 화면 레이아웃 정보를 등록한다.
     *  
     * @param  SessionTemplateVo 세션별 화면 레이아웃 정보
     * @return 번호
     * @throws ElException
     */
    public int insertSessionTemplate(SessionTemplateVo vo) throws ElException {    	
        return insert("com.demo.proworks.sessionTemplate.insertSessionTemplate", vo);
    }

    /**
     * 세션별 화면 레이아웃 정보를 갱신한다.
     *  
     * @param  SessionTemplateVo 세션별 화면 레이아웃 정보
     * @return 번호
     * @throws ElException
     */
    public int updateSessionTemplate(SessionTemplateVo vo) throws ElException {
        return update("com.demo.proworks.sessionTemplate.updateSessionTemplate", vo);
    }

    /**
     * 세션별 화면 레이아웃 정보를 삭제한다.
     *  
     * @param  SessionTemplateVo 세션별 화면 레이아웃 정보
     * @return 번호
     * @throws ElException
     */
    public int deleteSessionTemplate(SessionTemplateVo vo) throws ElException {
        return delete("com.demo.proworks.sessionTemplate.deleteSessionTemplate", vo);
    }

}
