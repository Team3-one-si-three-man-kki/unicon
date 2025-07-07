package com.demo.proworks.sessionTemplate.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.proworks.sessionTemplate.service.SessionTemplateService;
import com.demo.proworks.sessionTemplate.vo.SessionTemplateVo;
import com.demo.proworks.sessionTemplate.vo.SessionTemplateListVo;

import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.annotation.ElValidator;

/**  
 * @subject     : 세션별 화면 레이아웃 정보 관련 처리를 담당하는 컨트롤러
 * @description : 세션별 화면 레이아웃 정보 관련 처리를 담당하는 컨트롤러
 * @author      : 여경원
 * @since       : 2025/07/03
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/03			 여경원	 		최초 생성
 * 
 */
@Controller
public class SessionTemplateController {
	
    /** SessionTemplateService */
    @Resource(name = "sessionTemplateServiceImpl")
    private SessionTemplateService sessionTemplateService;
	
    
    /**
     * 세션별 화면 레이아웃 정보 목록을 조회합니다.
     *
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보
     * @return 목록조회 결과
     * @throws Exception
     */
    @ElService(key="TNU0002SessionTemplateList")
    @RequestMapping(value="TNU0002SessionTemplateList")    
    @ElDescription(sub="세션별 화면 레이아웃 정보 목록조회",desc="페이징을 처리하여 세션별 화면 레이아웃 정보 목록 조회를 한다.")               
    public SessionTemplateListVo selectListSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception {    	   	

        List<SessionTemplateVo> sessionTemplateList = sessionTemplateService.selectListSessionTemplate(sessionTemplateVo);                  
        long totCnt = sessionTemplateService.selectListCountSessionTemplate(sessionTemplateVo);
	
		SessionTemplateListVo retSessionTemplateList = new SessionTemplateListVo();
		retSessionTemplateList.setSessionTemplateVoList(sessionTemplateList); 
		retSessionTemplateList.setTotalCount(totCnt);
		retSessionTemplateList.setPageSize(sessionTemplateVo.getPageSize());
		retSessionTemplateList.setPageIndex(sessionTemplateVo.getPageIndex());

        return retSessionTemplateList;            
    }  
        
    /**
     * 세션별 화면 레이아웃 정보을 단건 조회 처리 한다.
     *
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보
     * @return 단건 조회 결과
     * @throws Exception
     */
    @ElService(key = "TNU0002SessionTemplateUpdView")    
    @RequestMapping(value="TNU0002SessionTemplateUpdView") 
    @ElDescription(sub = "세션별 화면 레이아웃 정보 갱신 폼을 위한 조회", desc = "세션별 화면 레이아웃 정보 갱신 폼을 위한 조회를 한다.")    
    public SessionTemplateVo selectSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception {
    	SessionTemplateVo selectSessionTemplateVo = sessionTemplateService.selectSessionTemplate(sessionTemplateVo);    	    
		
        return selectSessionTemplateVo;
    } 
 
    /**
     * 세션별 화면 레이아웃 정보를 등록 처리 한다.
     *
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보
     * @throws Exception
     */
    @ElService(key="TNU0002SessionTemplateIns")    
    @RequestMapping(value="TNU0002SessionTemplateIns")
    @ElDescription(sub="세션별 화면 레이아웃 정보 등록처리",desc="세션별 화면 레이아웃 정보를 등록 처리 한다.")
    public void insertSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception {    	 
    	sessionTemplateService.insertSessionTemplate(sessionTemplateVo);   
    }
       
    /**
     * 세션별 화면 레이아웃 정보를 갱신 처리 한다.
     *
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보
     * @throws Exception
     */
    @ElService(key="TNU0002SessionTemplateUpd")    
    @RequestMapping(value="TNU0002SessionTemplateUpd")    
    @ElValidator(errUrl="/sessionTemplate/sessionTemplateRegister", errContinue=true)
    @ElDescription(sub="세션별 화면 레이아웃 정보 갱신처리",desc="세션별 화면 레이아웃 정보를 갱신 처리 한다.")    
    public void updateSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception {  
 
    	sessionTemplateService.updateSessionTemplate(sessionTemplateVo);                                            
    }

    /**
     * 세션별 화면 레이아웃 정보를 삭제 처리한다.
     *
     * @param  sessionTemplateVo 세션별 화면 레이아웃 정보    
     * @throws Exception
     */
    @ElService(key = "TNU0002SessionTemplateDel")    
    @RequestMapping(value="TNU0002SessionTemplateDel")
    @ElDescription(sub = "세션별 화면 레이아웃 정보 삭제처리", desc = "세션별 화면 레이아웃 정보를 삭제 처리한다.")    
    public void deleteSessionTemplate(SessionTemplateVo sessionTemplateVo) throws Exception {
        sessionTemplateService.deleteSessionTemplate(sessionTemplateVo);
    }
   
}
