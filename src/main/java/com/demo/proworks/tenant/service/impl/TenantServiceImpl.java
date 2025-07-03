package com.demo.proworks.tenant.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.demo.proworks.tenant.service.TenantService;
import com.demo.proworks.tenant.vo.TenantVo;
import com.demo.proworks.tenant.dao.TenantDAO;

/**  
 * @subject     : 테넌트 관련 처리를 담당하는 ServiceImpl
 * @description	: 테넌트 관련 처리를 담당하는 ServiceImpl
 * @author      : LEEBYUNGWOOK
 * @since       : 2025/07/01
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/01			 LEEBYUNGWOOK	 		최초 생성
 * 
 */
@Service("tenantServiceImpl")
public class TenantServiceImpl implements TenantService {

    @Resource(name="tenantDAO")
    private TenantDAO tenantDAO;
	
	@Resource(name = "messageSource")
	private MessageSource messageSource;

    /**
     * 테넌트 목록을 조회합니다.
     *
     * @process
     * 1. 테넌트 페이징 처리하여 목록을 조회한다.
     * 2. 결과 List<TenantVo>을(를) 리턴한다.
     * 
     * @param  tenantVo 테넌트 TenantVo
     * @return 테넌트 목록 List<TenantVo>
     * @throws Exception
     */
	public List<TenantVo> selectListTenant(TenantVo tenantVo) throws Exception {
		List<TenantVo> list = tenantDAO.selectListTenant(tenantVo);	
	
		return list;
	}

    /**
     * 조회한 테넌트 전체 카운트
     *
     * @process
     * 1. 테넌트 조회하여 전체 카운트를 리턴한다.
     * 
     * @param  tenantVo 테넌트 TenantVo
     * @return 테넌트 목록 전체 카운트
     * @throws Exception
     */
	public long selectListCountTenant(TenantVo tenantVo) throws Exception {
		return tenantDAO.selectListCountTenant(tenantVo);
	}

    /**
     * 테넌트를 상세 조회한다.
     *
     * @process
     * 1. 테넌트를 상세 조회한다.
     * 2. 결과 TenantVo을(를) 리턴한다.
     * 
     * @param  tenantVo 테넌트 TenantVo
     * @return 단건 조회 결과
     * @throws Exception
     */
	public TenantVo selectTenant(TenantVo tenantVo) throws Exception {
		TenantVo resultVO = tenantDAO.selectTenant(tenantVo);			
        
        return resultVO;
	}

    /**
     * 테넌트를 등록 처리 한다.
     *
     * @process
     * 1. 테넌트를 등록 처리 한다.
     * 
     * @param  tenantVo 테넌트 TenantVo
     * @return 번호
     * @throws Exception
     */
	public int insertTenant(TenantVo tenantVo) throws Exception {
		return tenantDAO.insertTenant(tenantVo);	
	}
	
    /**
     * 테넌트를 갱신 처리 한다.
     *
     * @process
     * 1. 테넌트를 갱신 처리 한다.
     * 
     * @param  tenantVo 테넌트 TenantVo
     * @return 번호
     * @throws Exception
     */
	public int updateTenant(TenantVo tenantVo) throws Exception {				
		return tenantDAO.updateTenant(tenantVo);	   		
	}

    /**
     * 테넌트를 삭제 처리 한다.
     *
     * @process
     * 1. 테넌트를 삭제 처리 한다.
     * 
     * @param  tenantVo 테넌트 TenantVo
     * @return 번호
     * @throws Exception
     */
	public int deleteTenant(TenantVo tenantVo) throws Exception {
		return tenantDAO.deleteTenant(tenantVo);
	}
	
}
