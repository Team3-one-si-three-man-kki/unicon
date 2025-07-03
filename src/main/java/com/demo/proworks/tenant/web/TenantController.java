package com.demo.proworks.tenant.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.proworks.tenant.service.TenantService;
import com.demo.proworks.tenant.vo.TenantVo;
import com.demo.proworks.tenant.vo.TenantListVo;

import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.annotation.ElValidator;

/**  
 * @subject     : 테넌트 관련 처리를 담당하는 컨트롤러
 * @description : 테넌트 관련 처리를 담당하는 컨트롤러
 * @author      : LEEBYUNGWOOK
 * @since       : 2025/07/01
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/01			 LEEBYUNGWOOK	 		최초 생성
 * 
 */
@Controller
public class TenantController {
	
    /** TenantService */
    @Resource(name = "tenantServiceImpl")
    private TenantService tenantService;
	
    
    /**
     * 테넌트 목록을 조회합니다.
     *
     * @param  tenantVo 테넌트
     * @return 목록조회 결과
     * @throws Exception
     */
    @ElService(key="TNT0001List")
    @RequestMapping(value="TNT0001List")    
    @ElDescription(sub="테넌트 목록조회",desc="페이징을 처리하여 테넌트 목록 조회를 한다.")               
    public TenantListVo selectListTenant(TenantVo tenantVo) throws Exception {    	   	

        List<TenantVo> tenantList = tenantService.selectListTenant(tenantVo);                  
        long totCnt = tenantService.selectListCountTenant(tenantVo);
	
		TenantListVo retTenantList = new TenantListVo();
		retTenantList.setTenantVoList(tenantList); 
		retTenantList.setTotalCount(totCnt);
		retTenantList.setPageSize(tenantVo.getPageSize());
		retTenantList.setPageIndex(tenantVo.getPageIndex());

        return retTenantList;            
    }  
        
    /**
     * 테넌트을 단건 조회 처리 한다.
     *
     * @param  tenantVo 테넌트
     * @return 단건 조회 결과
     * @throws Exception
     */
    @ElService(key = "TNT0001UpdView")    
    @RequestMapping(value="TNT0001UpdView") 
    @ElDescription(sub = "테넌트 갱신 폼을 위한 조회", desc = "테넌트 갱신 폼을 위한 조회를 한다.")    
    public TenantVo selectTenant(TenantVo tenantVo) throws Exception {
    	TenantVo selectTenantVo = tenantService.selectTenant(tenantVo);    	    
		
        return selectTenantVo;
    } 
 
    /**
     * 테넌트를 등록 처리 한다.
     *
     * @param  tenantVo 테넌트
     * @throws Exception
     */
    @ElService(key="TNT0001Ins")    
    @RequestMapping(value="TNT0001Ins")
    @ElDescription(sub="테넌트 등록처리",desc="테넌트를 등록 처리 한다.")
    public void insertTenant(TenantVo tenantVo) throws Exception {    	 
    	tenantService.insertTenant(tenantVo);   
    }
       
    /**
     * 테넌트를 갱신 처리 한다.
     *
     * @param  tenantVo 테넌트
     * @throws Exception
     */
    @ElService(key="TNT0001Upd")    
    @RequestMapping(value="TNT0001Upd")    
    @ElValidator(errUrl="/tenant/tenantRegister", errContinue=true)
    @ElDescription(sub="테넌트 갱신처리",desc="테넌트를 갱신 처리 한다.")    
    public void updateTenant(TenantVo tenantVo) throws Exception {  
 
    	tenantService.updateTenant(tenantVo);                                            
    }

    /**
     * 테넌트를 삭제 처리한다.
     *
     * @param  tenantVo 테넌트    
     * @throws Exception
     */
    @ElService(key = "TNT0001Del")    
    @RequestMapping(value="TNT0001Del")
    @ElDescription(sub = "테넌트 삭제처리", desc = "테넌트를 삭제 처리한다.")    
    public void deleteTenant(TenantVo tenantVo) throws Exception {
        tenantService.deleteTenant(tenantVo);
    }
   
}
