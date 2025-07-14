package com.demo.proworks.tenantModule.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.demo.proworks.tenantModule.service.TenantModuleService;
import com.demo.proworks.tenantModule.vo.TenantModuleVo;
import com.demo.proworks.tenantModule.vo.TenantModuleDetailVo;
import com.demo.proworks.tenantModule.dao.TenantModuleDAO;

/**  
 * @subject     : 테넌트모듈정보 관련 처리를 담당하는 ServiceImpl
 * @description	: 테넌트모듈정보 관련 처리를 담당하는 ServiceImpl
 * @author      : 여경원
 * @since       : 2025/07/14
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/14			 여경원	 		최초 생성
 * 
 */
@Service("tenantModuleServiceImpl")
public class TenantModuleServiceImpl implements TenantModuleService {

    @Resource(name="tenantModuleDAO")
    private TenantModuleDAO tenantModuleDAO;
	
	@Resource(name = "messageSource")
	private MessageSource messageSource;

    /**
     * 테넌트모듈정보 목록을 조회합니다.
     *
     * @process
     * 1. 테넌트모듈정보 페이징 처리하여 목록을 조회한다.
     * 2. 결과 List<TenantModuleVo>을(를) 리턴한다.
     * 
     * @param  tenantModuleVo 테넌트모듈정보 TenantModuleVo
     * @return 테넌트모듈정보 목록 List<TenantModuleVo>
     * @throws Exception
     */
	public List<TenantModuleVo> selectListTenantModule(TenantModuleVo tenantModuleVo) throws Exception {
		List<TenantModuleVo> list = tenantModuleDAO.selectListTenantModule(tenantModuleVo);	
	
		return list;
	}

    /**
     * 조회한 테넌트모듈정보 전체 카운트
     *
     * @process
     * 1. 테넌트모듈정보 조회하여 전체 카운트를 리턴한다.
     * 
     * @param  tenantModuleVo 테넌트모듈정보 TenantModuleVo
     * @return 테넌트모듈정보 목록 전체 카운트
     * @throws Exception
     */
	public long selectListCountTenantModule(TenantModuleVo tenantModuleVo) throws Exception {
		return tenantModuleDAO.selectListCountTenantModule(tenantModuleVo);
	}

    /**
     * 테넌트모듈정보를 상세 조회한다.
     *
     * @process
     * 1. 테넌트모듈정보를 상세 조회한다.
     * 2. 결과 TenantModuleVo을(를) 리턴한다.
     * 
     * @param  tenantModuleVo 테넌트모듈정보 TenantModuleVo
     * @return 단건 조회 결과
     * @throws Exception
     */
	public TenantModuleVo selectTenantModule(TenantModuleVo tenantModuleVo) throws Exception {
		TenantModuleVo resultVO = tenantModuleDAO.selectTenantModule(tenantModuleVo);			
        
        return resultVO;
	}

    /**
     * 테넌트모듈정보를 등록 처리 한다.
     *
     * @process
     * 1. 테넌트모듈정보를 등록 처리 한다.
     * 
     * @param  tenantModuleVo 테넌트모듈정보 TenantModuleVo
     * @return 번호
     * @throws Exception
     */
	public int insertTenantModule(TenantModuleVo tenantModuleVo) throws Exception {
		return tenantModuleDAO.insertTenantModule(tenantModuleVo);	
	}
	
    /**
     * 테넌트모듈정보를 갱신 처리 한다.
     *
     * @process
     * 1. 테넌트모듈정보를 갱신 처리 한다.
     * 
     * @param  tenantModuleVo 테넌트모듈정보 TenantModuleVo
     * @return 번호
     * @throws Exception
     */
	public int updateTenantModule(TenantModuleVo tenantModuleVo) throws Exception {				
		return tenantModuleDAO.updateTenantModule(tenantModuleVo);	   		
	}

    /**
     * 테넌트모듈정보를 삭제 처리 한다.
     *
     * @process
     * 1. 테넌트모듈정보를 삭제 처리 한다.
     * 
     * @param  tenantModuleVo 테넌트모듈정보 TenantModuleVo
     * @return 번호
     * @throws Exception
     */
	public int deleteTenantModule(TenantModuleVo tenantModuleVo) throws Exception {
		return tenantModuleDAO.deleteTenantModule(tenantModuleVo);
	}

    /**
     * 테넌트가 보유한 모듈 상세 정보를 조회한다.
     *
     * @process
     * 1. 테넌트가 보유한 모듈 상세 정보를 MODULE과 JOIN하여 조회한다.
     * 2. 결과 List<TenantModuleDetailVo>을 리턴한다.
     * 
     * @param  tenantId 테넌트 ID
     * @return 테넌트가 보유한 모듈 상세 정보 목록
     * @throws Exception
     */
	public List<TenantModuleDetailVo> selectTenantModuleDetails(String tenantId) throws Exception {
		List<TenantModuleDetailVo> list = tenantModuleDAO.selectTenantModuleDetails(tenantId);
		return list;
	}
	
}
