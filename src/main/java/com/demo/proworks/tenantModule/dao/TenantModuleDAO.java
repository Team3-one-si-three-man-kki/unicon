package com.demo.proworks.tenantModule.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.inswave.elfw.exception.ElException;
import com.demo.proworks.tenantModule.vo.TenantModuleVo;
import com.demo.proworks.tenantModule.vo.TenantModuleDetailVo;
import com.demo.proworks.tenantModule.dao.TenantModuleDAO;

/**  
 * @subject     : 테넌트모듈정보 관련 처리를 담당하는 DAO
 * @description : 테넌트모듈정보 관련 처리를 담당하는 DAO
 * @author      : 여경원
 * @since       : 2025/07/14
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/14			 여경원	 		최초 생성
 * 
 */
@Repository("tenantModuleDAO")
public class TenantModuleDAO extends com.demo.proworks.cmmn.dao.ProworksDefaultAbstractDAO {

    /**
     * 테넌트모듈정보 상세 조회한다.
     *  
     * @param  TenantModuleVo 테넌트모듈정보
     * @return TenantModuleVo 테넌트모듈정보
     * @throws ElException
     */
    public TenantModuleVo selectTenantModule(TenantModuleVo vo) throws ElException {
        return (TenantModuleVo) selectByPk("com.demo.proworks.tenantModule.selectTenantModule", vo);
    }

    /**
     * 페이징을 처리하여 테넌트모듈정보 목록조회를 한다.
     *  
     * @param  TenantModuleVo 테넌트모듈정보
     * @return List<TenantModuleVo> 테넌트모듈정보
     * @throws ElException
     */
    public List<TenantModuleVo> selectListTenantModule(TenantModuleVo vo) throws ElException {      	
        return (List<TenantModuleVo>)list("com.demo.proworks.tenantModule.selectListTenantModule", vo);
    }

    /**
     * 테넌트모듈정보 목록 조회의 전체 카운트를 조회한다.
     *  
     * @param  TenantModuleVo 테넌트모듈정보
     * @return 테넌트모듈정보 조회의 전체 카운트
     * @throws ElException
     */
    public long selectListCountTenantModule(TenantModuleVo vo)  throws ElException{               
        return (Long)selectByPk("com.demo.proworks.tenantModule.selectListCountTenantModule", vo);
    }
        
    /**
     * 테넌트모듈정보를 등록한다.
     *  
     * @param  TenantModuleVo 테넌트모듈정보
     * @return 번호
     * @throws ElException
     */
    public int insertTenantModule(TenantModuleVo vo) throws ElException {    	
        return insert("com.demo.proworks.tenantModule.insertTenantModule", vo);
    }

    /**
     * 테넌트모듈정보를 갱신한다.
     *  
     * @param  TenantModuleVo 테넌트모듈정보
     * @return 번호
     * @throws ElException
     */
    public int updateTenantModule(TenantModuleVo vo) throws ElException {
        return update("com.demo.proworks.tenantModule.updateTenantModule", vo);
    }

    /**
     * 테넌트모듈정보를 삭제한다.
     *  
     * @param  TenantModuleVo 테넌트모듈정보
     * @return 번호
     * @throws ElException
     */
    public int deleteTenantModule(TenantModuleVo vo) throws ElException {
        return delete("com.demo.proworks.tenantModule.deleteTenantModule", vo);
    }

    /**
     * 테넌트가 보유한 모듈 상세 정보를 조회한다. (MODULE과 JOIN)
     *  
     * @param  tenantId 테넌트 ID
     * @return List<TenantModuleDetailVo> 테넌트가 보유한 모듈 상세 정보
     * @throws ElException
     */
    public List<TenantModuleDetailVo> selectTenantModuleDetails(String tenantId) throws ElException {
        return (List<TenantModuleDetailVo>) list("com.demo.proworks.tenantModule.selectTenantModuleDetails", tenantId);
    }

}
