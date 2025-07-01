package com.demo.proworks.module.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.proworks.module.service.ModuleService;
import com.demo.proworks.module.vo.ModuleVo;
import com.demo.proworks.module.vo.ModuleListVo;

import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.annotation.ElValidator;

/**  
 * @subject     : 모듈정보 관련 처리를 담당하는 컨트롤러
 * @description : 모듈정보 관련 처리를 담당하는 컨트롤러
 * @author      : 여경원
 * @since       : 2025/07/01
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/01			 여경원	 		최초 생성
 * 
 */
@Controller
public class ModuleController {
	
    /** ModuleService */
    @Resource(name = "moduleServiceImpl")
    private ModuleService moduleService;
	
    
    /**
     * 모듈정보 목록을 조회합니다.
     *
     * @param  moduleVo 모듈정보
     * @return 목록조회 결과
     * @throws Exception
     */
    @ElService(key="ModuleList")
    @RequestMapping(value="ModuleList")    
    @ElDescription(sub="모듈정보 목록조회",desc="페이징을 처리하여 모듈정보 목록 조회를 한다.")               
    public ModuleListVo selectListModule(ModuleVo moduleVo) throws Exception {    	   	

        List<ModuleVo> moduleList = moduleService.selectListModule(moduleVo);                  
        long totCnt = moduleService.selectListCountModule(moduleVo);
	
		ModuleListVo retModuleList = new ModuleListVo();
		retModuleList.setModuleVoList(moduleList); 
		retModuleList.setTotalCount(totCnt);
		retModuleList.setPageSize(moduleVo.getPageSize());
		retModuleList.setPageIndex(moduleVo.getPageIndex());

        return retModuleList;            
    }  
        
    /**
     * 모듈정보을 단건 조회 처리 한다.
     *
     * @param  moduleVo 모듈정보
     * @return 단건 조회 결과
     * @throws Exception
     */
    @ElService(key = "ModuleUpdView")    
    @RequestMapping(value="ModuleUpdView") 
    @ElDescription(sub = "모듈정보 갱신 폼을 위한 조회", desc = "모듈정보 갱신 폼을 위한 조회를 한다.")    
    public ModuleVo selectModule(ModuleVo moduleVo) throws Exception {
    	ModuleVo selectModuleVo = moduleService.selectModule(moduleVo);    	    
		
        return selectModuleVo;
    } 
 
    /**
     * 모듈정보를 등록 처리 한다.
     *
     * @param  moduleVo 모듈정보
     * @throws Exception
     */
    @ElService(key="ModuleIns")    
    @RequestMapping(value="ModuleIns")
    @ElDescription(sub="모듈정보 등록처리",desc="모듈정보를 등록 처리 한다.")
    public void insertModule(ModuleVo moduleVo) throws Exception {    	 
    	moduleService.insertModule(moduleVo);   
    }
       
    /**
     * 모듈정보를 갱신 처리 한다.
     *
     * @param  moduleVo 모듈정보
     * @throws Exception
     */
    @ElService(key="ModuleUpd")    
    @RequestMapping(value="ModuleUpd")    
    @ElValidator(errUrl="/module/moduleRegister", errContinue=true)
    @ElDescription(sub="모듈정보 갱신처리",desc="모듈정보를 갱신 처리 한다.")    
    public void updateModule(ModuleVo moduleVo) throws Exception {  
 
    	moduleService.updateModule(moduleVo);                                            
    }

    /**
     * 모듈정보를 삭제 처리한다.
     *
     * @param  moduleVo 모듈정보    
     * @throws Exception
     */
    @ElService(key = "ModuleDel")    
    @RequestMapping(value="ModuleDel")
    @ElDescription(sub = "모듈정보 삭제처리", desc = "모듈정보를 삭제 처리한다.")    
    public void deleteModule(ModuleVo moduleVo) throws Exception {
        moduleService.deleteModule(moduleVo);
    }
   
}
