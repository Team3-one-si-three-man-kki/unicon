package com.demo.proworks.tenantModule.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.demo.proworks.tenantModule.service.TenantModuleService;
import com.demo.proworks.tenantModule.vo.TenantModuleVo;
import com.demo.proworks.tenantModule.vo.TenantModuleListVo;
import com.demo.proworks.tenantModule.vo.TenantModuleDetailVo;
import com.demo.proworks.tenantModule.vo.TenantModuleDetailVoList;

import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.annotation.ElValidator;

/**  
 * @subject     : 테넌트모듈정보 관련 처리를 담당하는 컨트롤러
 * @description : 테넌트모듈정보 관련 처리를 담당하는 컨트롤러
 * @author      : 여경원
 * @since       : 2025/07/14
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/14			 여경원	 		최초 생성
 * 
 */
@Controller
public class TenantModuleController {
	
    /** TenantModuleService */
    @Resource(name = "tenantModuleServiceImpl")
    private TenantModuleService tenantModuleService;
	
    
    /**
     * 테넌트모듈정보 목록을 조회합니다.
     *
     * @param  tenantModuleVo 테넌트모듈정보
     * @return 목록조회 결과
     * @throws Exception
     */
    @ElService(key="TNU0001TenantModuleList")
    @RequestMapping(value="TNU0001TenantModuleList")    
    @ElDescription(sub="테넌트모듈정보 목록조회",desc="페이징을 처리하여 테넌트모듈정보 목록 조회를 한다.")               
    public TenantModuleListVo selectListTenantModule(TenantModuleVo tenantModuleVo) throws Exception {    	   	

        List<TenantModuleVo> tenantModuleList = tenantModuleService.selectListTenantModule(tenantModuleVo);                  
        long totCnt = tenantModuleService.selectListCountTenantModule(tenantModuleVo);
	
		TenantModuleListVo retTenantModuleList = new TenantModuleListVo();
		retTenantModuleList.setTenantModuleVoList(tenantModuleList); 
		retTenantModuleList.setTotalCount(totCnt);
		retTenantModuleList.setPageSize(tenantModuleVo.getPageSize());
		retTenantModuleList.setPageIndex(tenantModuleVo.getPageIndex());

        return retTenantModuleList;            
    }  
        
    /**
     * 테넌트모듈정보을 단건 조회 처리 한다.
     *
     * @param  tenantModuleVo 테넌트모듈정보
     * @return 단건 조회 결과
     * @throws Exception
     */
    @ElService(key = "TNU0001TenantModuleUpdView")    
    @RequestMapping(value="TNU0001TenantModuleUpdView") 
    @ElDescription(sub = "테넌트모듈정보 갱신 폼을 위한 조회", desc = "테넌트모듈정보 갱신 폼을 위한 조회를 한다.")    
    public TenantModuleVo selectTenantModule(TenantModuleVo tenantModuleVo) throws Exception {
    	TenantModuleVo selectTenantModuleVo = tenantModuleService.selectTenantModule(tenantModuleVo);    	    
		
        return selectTenantModuleVo;
    } 
 
    /**
     * 테넌트모듈정보를 등록 처리 한다.
     *
     * @param  tenantModuleVo 테넌트모듈정보
     * @throws Exception
     */
    @ElService(key="TNU0001TenantModuleIns")    
    @RequestMapping(value="TNU0001TenantModuleIns")
    @ElDescription(sub="테넌트모듈정보 등록처리",desc="테넌트모듈정보를 등록 처리 한다.")
    public void insertTenantModule(TenantModuleVo tenantModuleVo) throws Exception {    	 
    	tenantModuleService.insertTenantModule(tenantModuleVo);   
    }
       
    /**
     * 테넌트모듈정보를 갱신 처리 한다.
     *
     * @param  tenantModuleVo 테넌트모듈정보
     * @throws Exception
     */
    @ElService(key="TNU0001TenantModuleUpd")    
    @RequestMapping(value="TNU0001TenantModuleUpd")    
    @ElValidator(errUrl="/tenantModule/tenantModuleRegister", errContinue=true)
    @ElDescription(sub="테넌트모듈정보 갱신처리",desc="테넌트모듈정보를 갱신 처리 한다.")    
    public void updateTenantModule(TenantModuleVo tenantModuleVo) throws Exception {  
 
    	tenantModuleService.updateTenantModule(tenantModuleVo);                                            
    }

    /**
     * 테넌트모듈정보를 삭제 처리한다.
     *
     * @param  tenantModuleVo 테넌트모듈정보    
     * @throws Exception
     */
    @ElService(key = "TNU0001TenantModuleDel")    
    @RequestMapping(value="TNU0001TenantModuleDel")
    @ElDescription(sub = "테넌트모듈정보 삭제처리", desc = "테넌트모듈정보를 삭제 처리한다.")    
    public void deleteTenantModule(TenantModuleVo tenantModuleVo) throws Exception {
        tenantModuleService.deleteTenantModule(tenantModuleVo);
    }

    /**
     * 현재 로그인한 사용자의 테넌트가 보유한 모듈 목록을 조회합니다.
     *
     * @return 테넌트가 보유한 모듈 상세 정보 목록
     * @throws Exception
     */
    @ElService(key="TNU0001TenantModuleDetailList")
    @RequestMapping(value="TNU0001TenantModuleDetailList")    
    @ElDescription(sub="테넌트보유모듈 상세목록조회",desc="현재 로그인한 사용자의 테넌트가 보유한 모듈 상세 정보를 조회한다.")               
    public TenantModuleDetailVoList selectTenantModuleDetailList() throws Exception {
        
        System.out.println("🚀 TenantModuleDetailList 컨트롤러 호출됨!");
        
        // 현재 로그인한 사용자의 테넌트 ID 가져오기
        HttpServletRequest request = ((ServletRequestAttributes) 
            RequestContextHolder.currentRequestAttributes()).getRequest();
        String tenantId = (String) request.getAttribute("tenantId");
        
        System.out.println("📋 테넌트 ID: " + tenantId);
        
        if (tenantId == null || tenantId.isEmpty()) {
            System.out.println("❌ 테넌트 정보를 찾을 수 없습니다.");
            // 테스트용으로 임시 테넌트 ID 사용
            tenantId = "test-tenant-001";
            System.out.println("🧪 테스트용 테넌트 ID 사용: " + tenantId);
        }
        
        // 테넌트가 보유한 모듈 상세 정보 조회
        List<TenantModuleDetailVo> tenantModuleDetailList = 
            tenantModuleService.selectTenantModuleDetails(tenantId);
        
        System.out.println("📊 조회된 모듈 수: " + tenantModuleDetailList.size());
        
        for (TenantModuleDetailVo vo : tenantModuleDetailList) {
            System.out.println("🧩 모듈: " + vo.getModuleId() + " - " + vo.getName() + " (" + vo.getIcon() + ")");
        }
        
        // 응답 객체 생성
        TenantModuleDetailVoList retTenantModuleDetailList = new TenantModuleDetailVoList();
        retTenantModuleDetailList.setTenantModuleDetailVoList(tenantModuleDetailList);
        retTenantModuleDetailList.setTotalCount(tenantModuleDetailList.size());
        
        System.out.println("✅ 응답 데이터 생성 완료 - 총 " + tenantModuleDetailList.size() + "개 모듈");
        
        return retTenantModuleDetailList;
    }
   
}
