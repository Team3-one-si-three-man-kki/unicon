package com.demo.proworks.user.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.proworks.jwt.JwtUtil;
import com.demo.proworks.jwt.TokenBlacklistService;
import com.demo.proworks.user.service.SignupService;
import com.demo.proworks.user.service.UserService;
import com.demo.proworks.user.vo.UserVo;
import com.demo.proworks.user.vo.UserListVo;
import com.demo.proworks.user.vo.UserSignupVo;
import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.annotation.ElValidator;
import com.inswave.elfw.log.AppLog;
import com.inswave.elfw.login.LoginException;
import com.inswave.elfw.login.LoginInfo;
import com.inswave.elfw.login.LoginProcessor;
import com.inswave.elfw.util.ElBeanUtils;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

	@Resource(name = "jwtUtil")
	private JwtUtil jwtUtil;
	
    /** UserService */
    @Resource(name = "userServiceImpl")
    private UserService userService;
    
     @Resource(name = "signupServiceImpl")
    private SignupService signupService;
	
	@Resource(name= "loginProcess")
	protected LoginProcessor loginProcess;
    
    @Resource(name= "tokenBlacklistService")
    private TokenBlacklistService tokenBlacklistService;
    
    
    @ElService(key = "TNU0000Login")
	@RequestMapping(value = "TNU0000Login")
	@ElDescription(sub = "로그인처리", desc = "이메일 로그인처리")
	public void login(com.demo.proworks.user.vo.UserLoginVo loginVo, HttpServletRequest request) throws Exception {
    	String email = loginVo.getEmail();
    	String password = loginVo.getPassword();
    	String tenantId = "2";

    	try {
        // 1. 로그인 처리 (ProworksLoginAdapter + ProworksSessionDataAdapter 호출)
        LoginInfo info = loginProcess.processLogin(request, email, password, tenantId);
        AppLog.debug("로그인 처리 결과: " + info);
        
        // 2. 성공 여부만 판별. 세션 어댑터가 JWT 헤더 설정을 이미 수행함
        if (!info.isSuc()) {
            throw new LoginException("EL.ERROR.LOGIN.0001");
        }
        
    } catch (Exception e) {
        AppLog.error("로그인 처리 중 오류 발생", e);
        throw e;
    }
    }
    
    /**
 * 이메일 중복 검사 API
 */
@ElService(key = "TNU0000CheckEmail")
@RequestMapping(value = "TNU0000CheckEmail")
@ElDescription(sub = "이메일중복검사", desc = "이메일 사용 가능 여부 확인")
public void checkEmailAvailability(UserSignupVo signupVo, HttpServletRequest request) throws Exception {
    String email = signupVo.getEmail();
    
    System.out.println("이메일 중복 검사 요청: " + email);
    
    try {
        boolean available = signupService.isEmailAvailable(email);
        
        request.setAttribute("emailCheckResult", available ? "available" : "unavailable");
        request.setAttribute("email", email);
        
        System.out.println("이메일 중복 검사 결과: " + (available ? "사용가능" : "사용불가"));
        
    } catch (Exception e) {
        System.err.println("이메일 중복 검사 실패: " + e.getMessage());
        request.setAttribute("emailCheckResult", "error");
        request.setAttribute("errorMessage", e.getMessage());
        
        throw e;
    }
}

@ElService(key = "TNU0000Signup")
@RequestMapping(value = "TNU0000Signup")
@ElDescription(sub = "회원가입처리", desc = "사용자 회원가입 처리")
public void signup(UserSignupVo signupVo, HttpServletRequest request) throws Exception {

    System.out.println("회원가입 요청 정보: " + signupVo.toString());
    System.out.println("회원가입 컨트롤러 진입~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    
    try {
        // SignupService 인터페이스를 통한 회원가입 처리
        UserVo result = signupService.processSignup(signupVo);
        
        System.out.println("회원가입 성공 - 사용자ID: " + result.getUserId() + 
                         ", 테넌트ID: " + result.getTenantId() + "======================================");
                         
        AppLog.debug("- 회원가입 결과 : " + result.toString());
        
        // 성공 응답 처리
        request.setAttribute("signupResult", "success");
        request.setAttribute("userId", result.getUserId());
        request.setAttribute("tenantId", result.getTenantId());
        
    } catch (Exception e) {
        System.err.println("회원가입 실패: " + e.getMessage());
        AppLog.error("- 회원가입 오류 : " + e.getMessage(), e);
        
        // 실패 응답 처리
        request.setAttribute("signupResult", "fail");
        request.setAttribute("errorMessage", e.getMessage());
        
        throw e;
    }
}
    
     @ElService(key = "TNU0000Logout")
    @RequestMapping(value = "TNU0000Logout")
    @ElDescription(sub = "로그아웃처리", desc = "로그아웃 및 토큰 블랙리스트 처리")
    public Map<String, Object> logout(HttpServletRequest request) throws Exception {
        
         Map<String, Object> result = new HashMap<>();
    
    try {
        // 1. Authorization 헤더에서 토큰 추출
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            // 2. 토큰을 블랙리스트에 추가
            long remainingTime = tokenBlacklistService.getTokenRemainingTime(token);
            if (remainingTime > 0) {
                tokenBlacklistService.addToBlacklist(token, remainingTime);
                AppLog.debug("토큰이 블랙리스트에 추가됨: " + token);
            }
        }
        
        // 3. Refresh Token 쿠키 삭제
        clearRefreshTokenCookie();
        
        result.put("success", true);
        result.put("message", "로그아웃이 성공했습니다.");
        AppLog.debug("[Logout] 로그아웃 성공 - 토큰 블랙리스트 처리 완료");
        
    } catch (Exception e) {
        AppLog.error("로그아웃 중 오류: " + e.getMessage(), e);
        result.put("success", false);
        result.put("message", "로그아웃 처리 중 오류가 발생했습니다.");
    }
    
    return result;
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
    
     private void setTokensToResponse(String accessToken, String refreshToken) {
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletResponse response = attr.getResponse();
            if (response != null) {
                // Access Token 헤더
                response.setHeader("Authorization", "Bearer " + accessToken);
                response.setHeader("Access-Control-Expose-Headers", "Authorization");
                response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
                response.setHeader("Access-Control-Allow-Origin", "*");
                // Refresh Token 쿠키
                Cookie cookie = new Cookie("refreshToken", refreshToken);
                cookie.setHttpOnly(true);
                cookie.setSecure(false);
                cookie.setPath("/");
                cookie.setMaxAge(7 * 24 * 60 * 60);
                response.addCookie(cookie);
                AppLog.debug("토큰 응답 설정 완료");
            }
        } catch (Exception e) {
            AppLog.error("토큰 설정 중 오류: " + e.getMessage(), e);
        }
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("refreshToken".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    private void clearRefreshTokenCookie() {
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletResponse response = attr.getResponse();
            if (response != null) {
                Cookie cookie = new Cookie("refreshToken", null);
                cookie.setHttpOnly(true);
                cookie.setSecure(false);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
                AppLog.debug("Refresh Token 쿠키 삭제 완료");
            }
        } catch (Exception e) {
            AppLog.error("쿠키 삭제 중 오류: " + e.getMessage(), e);
        }
    }
    
    
   
}
