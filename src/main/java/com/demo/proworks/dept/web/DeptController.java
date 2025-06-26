package com.demo.proworks.dept.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.proworks.dept.service.DeptService;
import com.demo.proworks.dept.vo.DeptVo;
import com.demo.proworks.dept.vo.DeptListVo;

import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.annotation.ElValidator;
import org.springframework.web.bind.annotation.RequestMethod;

/**  
 * @subject     : 부서 정보 관련 처리를 담당하는 컨트롤러
 * @description : 부서 정보 관련 처리를 담당하는 컨트롤러
 * @author      : LEEJAEWON
 * @since       : 2025/06/10
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/06/10			 LEEJAEWON	 		최초 생성
 * 
 */
@Controller
public class DeptController {
	
    /** DeptService */
    @Resource(name = "deptServiceImpl")
    private DeptService deptService;
	
    
    /**
     * 부서 정보 목록을 조회합니다.
     *
     * @param  deptVo 부서 정보
     * @return 목록조회 결과
     * @throws Exception
     */
    @ElService(key="SCV0001List")
    @RequestMapping(value="SCV0001List")    
    @ElDescription(sub="부서 정보 목록조회",desc="페이징을 처리하여 부서 정보 목록 조회를 한다.")               
    public DeptListVo selectListDept(DeptVo deptVo) throws Exception {    	   	

        List<DeptVo> deptList = deptService.selectListDept(deptVo);                  
        long totCnt = deptService.selectListCountDept(deptVo);
	
		DeptListVo retDeptList = new DeptListVo();
		retDeptList.setDeptVoList(deptList); 
		retDeptList.setTotalCount(totCnt);
		retDeptList.setPageSize(deptVo.getPageSize());
		retDeptList.setPageIndex(deptVo.getPageIndex());

        return retDeptList;            
    }  
        
    /**
     * 부서 정보을 단건 조회 처리 한다.
     *
     * @param  deptVo 부서 정보
     * @return 단건 조회 결과
     * @throws Exception
     */
    @ElService(key = "SCV0001UpdView")    
    @RequestMapping(value="SCV0001UpdView") 
    @ElDescription(sub = "부서 정보 갱신 폼을 위한 조회", desc = "부서 정보 갱신 폼을 위한 조회를 한다.")    
    public DeptVo selectDept(DeptVo deptVo) throws Exception {
    	DeptVo selectDeptVo = deptService.selectDept(deptVo);    	    
		
        return selectDeptVo;
    } 
 
    /**
     * 부서 정보를 등록 처리 한다.
     *
     * @param  deptVo 부서 정보
     * @throws Exception
     */
    @ElService(key="SCV0001Ins")    
    @RequestMapping(value="SCV0001Ins")
    @ElDescription(sub="부서 정보 등록처리",desc="부서 정보를 등록 처리 한다.")
    public void insertDept(DeptVo deptVo) throws Exception {    	 
    	deptService.insertDept(deptVo);   
    }
    
    /**
     * 부서 정보를 등록 처리 한다.
     *
     * @param  deptVo 부서 정보
     * @throws Exception
     */
    @ElService(key = "SCV0001InsTest")    
    @RequestMapping(value = "SCV0001InsTest")
    @ElDescription(sub = "부서 정보 등록처리", desc = "부서 정보를 등록 처리 한다. (List 전송 테스트)")
    public void insertDeptTest(DeptListVo deptVoList) throws Exception {    	 
//    	deptService.insertDept(deptVo);   
    	
    	    	
    	int cnt = deptVoList.getDeptVoList().size();
    	
    	for(int _i=0; _i<cnt; _i++){
    		deptService.insertDept(deptVoList.getDeptVoList().get(_i));
    	}
    	
    }
       
    /**
     * 부서 정보를 갱신 처리 한다.
     *
     * @param  deptVo 부서 정보
     * @throws Exception
     */
    @ElService(key="SCV0001Upd")    
    @RequestMapping(value="SCV0001Upd")    
    @ElValidator(errUrl="/dept/deptRegister", errContinue=true)
    @ElDescription(sub="부서 정보 갱신처리",desc="부서 정보를 갱신 처리 한다.")    
    public void updateDept(DeptVo deptVo) throws Exception {  
 
    	deptService.updateDept(deptVo);                                            
    }
    
    /**
     * 부서 정보를 갱신 처리 한다.
     *
     * @param  deptVo 부서 정보
     * @throws Exception
     */
    @ElService(key = "SCV0001UpdTest")    
    @RequestMapping(value = "SCV0001UpdTest")    
    @ElDescription(sub = "부서 정보 갱신처리", desc = "부서 정보를 갱신 처리 한다. (List 전송 테스트)")    
    public void updateDeptTest(DeptListVo deptVoList) throws Exception {  
    	System.out.println("체크>>>>>>>>>>>>>"+deptVoList.getDeptVoList().toString());
    	
    	int cnt = deptVoList.getDeptVoList().size();
    	
    	for(int _i=0; _i<cnt; _i++){
    		deptService.updateDept(deptVoList.getDeptVoList().get(_i));
    	}
    	
 
    	//deptService.updateDept(deptVoList);                                            
    }
    
    
    /**
     * 부서 정보를 삭제 처리한다.
     *
     * @param  deptVo 부서 정보    
     * @throws Exception
     */
    @ElService(key = "SCV0001Del")    
    @RequestMapping(value="SCV0001Del")
    @ElDescription(sub = "부서 정보 삭제처리", desc = "부서 정보를 삭제 처리한다.")    
    public void deleteDept(DeptVo deptVo) throws Exception {
        deptService.deleteDept(deptVo);
    }
    
    
    @ElService(key = "SCV0001ModiTest")    
    @RequestMapping(value = "SCV0001ModiTest")
    @ElDescription(sub = "부서 정보 통합저장", desc = "부서 정보를 통합 저장 처리한다.(List Modi 테스트)")    
    public void saveAllDeptTest(DeptListVo deptVoList) throws Exception {
    	int cnt = deptVoList.getDeptVoList().size();

    	for(int _i=0; _i<cnt; _i++){
    		String _rowStatus = deptVoList.getDeptVoList().get(_i).getRowStatus();
    		System.out.println(_rowStatus + " <<<<<<<<<<<<<<<<<<<<<<<<,");
    		
    		if(_rowStatus.equals("C")){
    			deptService.insertDept(deptVoList.getDeptVoList().get(_i));
    		}else if(_rowStatus.equals("U")){
    			deptService.updateDept(deptVoList.getDeptVoList().get(_i));    		
    		}else if(_rowStatus.equals("D")){
    			deptService.deleteDept(deptVoList.getDeptVoList().get(_i));    		
    		}
    	}
    }
   
}
