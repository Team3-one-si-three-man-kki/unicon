package com.demo.proworks.tenant.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.inswave.elfw.exception.ElException;
import com.demo.proworks.tenant.vo.TenantVo;
import com.demo.proworks.tenant.dao.TenantDAO;

/**  
 * @subject     : 테넌트 관련 처리를 담당하는 DAO
 * @description : 테넌트 관련 처리를 담당하는 DAO
 * @author      : LEEBYUNGWOOK
 * @since       : 2025/07/01
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/01			 LEEBYUNGWOOK	 		최초 생성
 * 
 */
@Repository("tenantDAO")
public class TenantDAO extends com.demo.proworks.cmmn.dao.ProworksDefaultAbstractDAO {

    /**
     * 테넌트 상세 조회한다.
     *  
     * @param  TenantVo 테넌트
     * @return TenantVo 테넌트
     * @throws ElException
     */
    public TenantVo selectTenant(TenantVo vo) throws ElException {
        return (TenantVo) selectByPk("com.demo.proworks.tenant.selectTenant", vo);
    }

    /**
     * 페이징을 처리하여 테넌트 목록조회를 한다.
     *  
     * @param  TenantVo 테넌트
     * @return List<TenantVo> 테넌트
     * @throws ElException
     */
    public List<TenantVo> selectListTenant(TenantVo vo) throws ElException {      	
        return (List<TenantVo>)list("com.demo.proworks.tenant.selectListTenant", vo);
    }

    /**
     * 테넌트 목록 조회의 전체 카운트를 조회한다.
     *  
     * @param  TenantVo 테넌트
     * @return 테넌트 조회의 전체 카운트
     * @throws ElException
     */
    public long selectListCountTenant(TenantVo vo)  throws ElException{               
        return (Long)selectByPk("com.demo.proworks.tenant.selectListCountTenant", vo);
    }
        
    /**
     * 테넌트를 등록한다.
     *  
     * @param  TenantVo 테넌트
     * @return 번호
     * @throws ElException
     */
    public int insertTenant(TenantVo vo) throws ElException {    	
        return insert("com.demo.proworks.tenant.insertTenant", vo);
    }

    /**
     * 테넌트를 갱신한다.
     *  
     * @param  TenantVo 테넌트
     * @return 번호
     * @throws ElException
     */
    public int updateTenant(TenantVo vo) throws ElException {
        return update("com.demo.proworks.tenant.updateTenant", vo);
    }

    /**
     * 테넌트를 삭제한다.
     *  
     * @param  TenantVo 테넌트
     * @return 번호
     * @throws ElException
     */
    public int deleteTenant(TenantVo vo) throws ElException {
        return delete("com.demo.proworks.tenant.deleteTenant", vo);
    }

}
