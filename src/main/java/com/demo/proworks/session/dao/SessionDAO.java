package com.demo.proworks.session.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.inswave.elfw.exception.ElException;
import com.demo.proworks.session.vo.SessionVo;
import com.demo.proworks.session.dao.SessionDAO;

/**  
 * @subject     : 세션정보 관련 처리를 담당하는 DAO
 * @description : 세션정보 관련 처리를 담당하는 DAO
 * @author      : 여경원
 * @since       : 2025/07/04
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/04			 여경원	 		최초 생성
 * 
 */
@Repository("sessionDAO")
public class SessionDAO extends com.demo.proworks.cmmn.dao.ProworksDefaultAbstractDAO {

    /**
     * 세션정보 상세 조회한다.
     *  
     * @param  SessionVo 세션정보
     * @return SessionVo 세션정보
     * @throws ElException
     */
    public SessionVo selectSession(SessionVo vo) throws ElException {
        return (SessionVo) selectByPk("com.demo.proworks.session.selectSession", vo);
    }

    /**
     * 페이징을 처리하여 세션정보 목록조회를 한다.
     *  
     * @param  SessionVo 세션정보
     * @return List<SessionVo> 세션정보
     * @throws ElException
     */
    public List<SessionVo> selectListSession(SessionVo vo) throws ElException {      	
        return (List<SessionVo>)list("com.demo.proworks.session.selectListSession", vo);
    }

    /**
     * 세션정보 목록 조회의 전체 카운트를 조회한다.
     *  
     * @param  SessionVo 세션정보
     * @return 세션정보 조회의 전체 카운트
     * @throws ElException
     */
    public long selectListCountSession(SessionVo vo)  throws ElException{               
        return (Long)selectByPk("com.demo.proworks.session.selectListCountSession", vo);
    }
        
    /**
     * 세션정보를 등록한다.
     *  
     * @param  SessionVo 세션정보
     * @return 번호
     * @throws ElException
     */
    public int insertSession(SessionVo vo) throws ElException {    	
        return insert("com.demo.proworks.session.insertSession", vo);
    }

    /**
     * 세션정보를 갱신한다.
     *  
     * @param  SessionVo 세션정보
     * @return 번호
     * @throws ElException
     */
    public int updateSession(SessionVo vo) throws ElException {
        return update("com.demo.proworks.session.updateSession", vo);
    }

    /**
     * 세션정보를 삭제한다.
     *  
     * @param  SessionVo 세션정보
     * @return 번호
     * @throws ElException
     */
    public int deleteSession(SessionVo vo) throws ElException {
        return delete("com.demo.proworks.session.deleteSession", vo);
    }

}
