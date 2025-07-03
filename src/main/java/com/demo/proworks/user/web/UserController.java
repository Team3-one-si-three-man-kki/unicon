package com.demo.proworks.user.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.proworks.user.service.UserService;
import com.demo.proworks.user.vo.UserVo;
import com.demo.proworks.user.vo.UserListVo;

import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.annotation.ElValidator;
import com.inswave.elfw.log.AppLog;
import com.inswave.elfw.login.LoginInfo;
import com.inswave.elfw.login.LoginProcessor;
import org.springframework.web.bind.annotation.RequestMethod;

/**  
 * @subject     : 테넌트유저 관련 처리를 담당하는 컨트롤러
 * @description : 테넌트유저 관련 처리를 담당하는 컨트롤러
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
public class UserController {
	
    /** UserService */
    @Resource(name = "userServiceImpl")
    private UserService userService;
	
	@Resource(name= "loginProcess")
	protected LoginProcessor loginProcess;
    
    
    
    @ElService(key = "TNU0000Login")
	@RequestMapping(value = "TNU0000Login")
	@ElDescription(sub = "로그인처리", desc = "이메일 로그인처리")
	public void login(com.demo.proworks.user.vo.UserLoginVo loginVo, HttpServletRequest request) throws Exception {
    	String email = loginVo.getEmail();
    	String password = loginVo.getPassword();
    	System.out.println("겟야이디 패스워드 "+email+",,,,,,,,"+password+"=========================");
    	System.out.println("컨트롤러는잘타는중~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    	
    	LoginInfo info = loginProcess.processLogin(request, email, password);
    	System.out.println("로그인 인포에 정보 잘담기는거야?"+info+"======================================");
    	AppLog.debug("- Login 정보 : " + info.toString());
    	System.out.println("로그인 성공 여부: " + info.isSuc());
    }
    
    
    
    /**
     * 테넌트유저 목록을 조회합니다.
     *
     * @param  userVo 테넌트유저
     * @return 목록조회 결과
     * @throws Exception
     */
    @ElService(key = "TNU0002List")
    @RequestMapping(value = "TNU0002List")    
    @ElDescription(sub = "테넌트유저 목록조회", desc = "페이징을 처리하여 테넌트유저 목록 조회를 한다.")               
    public UserListVo selectListUser(UserVo userVo) throws Exception {    	   	

        List<UserVo> userList = userService.selectListUser(userVo);                  
        long totCnt = userService.selectListCountUser(userVo);
	
		UserListVo retUserList = new UserListVo();
		retUserList.setUserVoList(userList); 
		retUserList.setTotalCount(totCnt);
		retUserList.setPageSize(userVo.getPageSize());
		retUserList.setPageIndex(userVo.getPageIndex());

        return retUserList;            
    }  
        
    /**
     * 테넌트유저을 단건 조회 처리 한다.
     *
     * @param  userVo 테넌트유저
     * @return 단건 조회 결과
     * @throws Exception
     */
    @ElService(key = "TNU0001UpdView")    
    @RequestMapping(value="TNU0001UpdView") 
    @ElDescription(sub = "테넌트유저 갱신 폼을 위한 조회", desc = "테넌트유저 갱신 폼을 위한 조회를 한다.")    
    public UserVo selectUser(UserVo userVo) throws Exception {
    	UserVo selectUserVo = userService.selectUser(userVo);    	    
		
        return selectUserVo;
    } 
 
    /**
     * 테넌트유저를 등록 처리 한다.
     *
     * @param  userVo 테넌트유저
     * @throws Exception
     */
    @ElService(key = "TNU0002Ins")    
    @RequestMapping(value = "TNU0002Ins")
    @ElDescription(sub = "테넌트유저 등록처리", desc = "테넌트유저를 등록 처리 한다.")
    public void insertUser(UserVo userVo) throws Exception {    	 
    	userService.insertUser(userVo);   
    }
       
    /**
     * 테넌트유저를 갱신 처리 한다.
     *
     * @param  userVo 테넌트유저
     * @throws Exception
     */
    @ElService(key = "TNU0002Upd")    
    @RequestMapping(value = "TNU0002Upd")    
    @ElDescription(sub = "테넌트유저 갱신처리", desc = "테넌트유저를 갱신 처리 한다.")    
    public void updateUser(UserVo userVo) throws Exception {  
 
    	userService.updateUser(userVo);                                            
    }

    /**
     * 테넌트유저를 삭제 처리한다.
     *
     * @param  userVo 테넌트유저    
     * @throws Exception
     */
    @ElService(key = "TNU0002Del")    
    @RequestMapping(value = "TNU0002Del")
    @ElDescription(sub = "테넌트유저 삭제처리", desc = "테넌트유저를 삭제 처리한다.")    
    public void deleteUser(UserVo userVo) throws Exception {
        userService.deleteUser(userVo);
    }
   
}
