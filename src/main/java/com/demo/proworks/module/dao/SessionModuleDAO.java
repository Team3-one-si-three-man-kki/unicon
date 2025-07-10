package com.demo.proworks.module.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.inswave.elfw.exception.ElException;
import com.demo.proworks.module.vo.SessionModuleVo;
import com.demo.proworks.module.dao.SessionModuleDAO;

/**  
 * @subject     : 세션모듈설정정보 관련 처리를 담당하는 DAO
 * @description : 세션모듈설정정보 관련 처리를 담당하는 DAO
 * @author      : 여경원
 * @since       : 2025/07/02
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/02			 여경원	 		최초 생성
 * 
 */
@Repository("sessionModuleDAO")
public class SessionModuleDAO extends com.demo.proworks.cmmn.dao.ProworksDefaultAbstractDAO {

    /**
     * 세션모듈설정정보 상세 조회한다.
     *  
     * @param  SessionModuleVo 세션모듈설정정보
     * @return SessionModuleVo 세션모듈설정정보
     * @throws ElException
     */
    public SessionModuleVo selectSessionModule(SessionModuleVo vo) throws ElException {
        return (SessionModuleVo) selectByPk("com.demo.proworks.module.selectSessionModule", vo);
    }

    /**
     * 페이징을 처리하여 세션모듈설정정보 목록조회를 한다.
     *  
     * @param  SessionModuleVo 세션모듈설정정보
     * @return List<SessionModuleVo> 세션모듈설정정보
     * @throws ElException
     */
    public List<SessionModuleVo> selectListSessionModule(SessionModuleVo vo) throws ElException {      	
        return (List<SessionModuleVo>)list("com.demo.proworks.module.selectListSessionModule", vo);
    }

    /**
     * 세션모듈설정정보 목록 조회의 전체 카운트를 조회한다.
     *  
     * @param  SessionModuleVo 세션모듈설정정보
     * @return 세션모듈설정정보 조회의 전체 카운트
     * @throws ElException
     */
    public long selectListCountSessionModule(SessionModuleVo vo)  throws ElException{               
        return (Long)selectByPk("com.demo.proworks.module.selectListCountSessionModule", vo);
    }
        
    /**
     * 세션모듈설정정보를 등록한다.
     *  
     * @param  SessionModuleVo 세션모듈설정정보
     * @return 번호
     * @throws ElException
     */
    public int insertSessionModule(SessionModuleVo vo) throws ElException {    	
        return insert("com.demo.proworks.module.insertSessionModule", vo);
    }

    /**
     * 세션모듈설정정보를 갱신한다.
     *  
     * @param  SessionModuleVo 세션모듈설정정보
     * @return 번호
     * @throws ElException
     */
    public int updateSessionModule(SessionModuleVo vo) throws ElException {
        return update("com.demo.proworks.module.updateSessionModule", vo);
    }

    /**
     * 세션모듈설정정보를 삭제한다.
     *  
     * @param  SessionModuleVo 세션모듈설정정보
     * @return 번호
     * @throws ElException
     */
    public int deleteSessionModule(SessionModuleVo vo) throws ElException {
        return delete("com.demo.proworks.module.deleteSessionModule", vo);
    }

}
