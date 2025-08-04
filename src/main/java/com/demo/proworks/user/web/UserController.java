package com.demo.proworks.user.web;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.demo.proworks.jwt.JwtUtil;
import com.demo.proworks.jwt.TokenBlacklistService;
import com.demo.proworks.user.service.SignupService;
import com.demo.proworks.user.service.UserService;
import com.demo.proworks.user.vo.UserListVo;
import com.demo.proworks.user.vo.UserSignupVo;
import com.demo.proworks.user.vo.UserVo;
import com.demo.proworks.util.PasswordEncryptUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.log.AppLog;
import com.inswave.elfw.login.LoginException;
import com.inswave.elfw.login.LoginInfo;
import com.inswave.elfw.login.LoginProcessor;

/**
 * @subject : 테넌트유저 관련 처리를 담당하는 컨트롤러
 * @description : 테넌트유저 관련 처리를 담당하는 컨트롤러
 * @author : LEEBYUNGWOOK
 * @since : 2025/07/01
 * @modification ===========================================================
 *               DATE AUTHOR DESC
 *               ===========================================================
 *               2025/07/01 LEEBYUNGWOOK 최초 생성
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

	@Resource(name = "loginProcess")
	protected LoginProcessor loginProcess;

	@Resource(name = "tokenBlacklistService")
	private TokenBlacklistService tokenBlacklistService;

	@Resource(name = "passwordEncryptUtil")
	private PasswordEncryptUtil passwordEncryptUtil;

	@ElService(key = "TNU0000Login")
	@RequestMapping(value = "TNU0000Login")
	@ElDescription(sub = "로그인처리", desc = "이메일 로그인처리")
	public void login(com.demo.proworks.user.vo.UserLoginVo loginVo, HttpServletRequest request) throws Exception {
		String email = loginVo.getEmail();
		String password = loginVo.getPassword();
		String subDomain = loginVo.getSubDomain();

		try {
			// 1. 로그인 처리 (ProworksLoginAdapter + ProworksSessionDataAdapter 호출)
			LoginInfo info = loginProcess.processLogin(request, email, password, subDomain);
		
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
	public ResponseEntity<String> checkEmailAvailability(UserSignupVo signupVo) {
    String email = signupVo.getEmail();
   
    try {
        boolean available = signupService.isEmailAvailable(email);
        
        String message = available 
                ? "사용 가능한 이메일입니다." 
                : "이미 사용 중인 이메일입니다.";

        return ResponseEntity.ok(message);

    } catch (Exception e) {
       
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("이메일 중복 검사 중 오류가 발생했습니다.");
    }
}

	@ElService(key = "TNU0000Signup")
	@RequestMapping(value = "TNU0000Signup")
	@ElDescription(sub = "회원가입처리", desc = "사용자 회원가입 처리")
	public void signup(UserSignupVo signupVo, HttpServletRequest request) throws Exception {

		try {
			// SignupService 인터페이스를 통한 회원가입 처리
			UserVo result = signupService.processSignup(signupVo);

			AppLog.debug("- 회원가입 결과 : " + result.toString());

			// 성공 응답 처리
			request.setAttribute("signupResult", "success");
			request.setAttribute("userId", result.getUserId());
			request.setAttribute("tenantId", result.getTenantId());

		} catch (Exception e) {
		
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
		String userId = null;

		try {
			// 1. Authorization 헤더에서 토큰 추출
			String authHeader = request.getHeader("Authorization");
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = authHeader.substring(7);
				
				 userId = jwtUtil.getUserIdFromToken(token);

				// 2. 토큰을 블랙리스트에 추가
				long remainingTime = tokenBlacklistService.getTokenRemainingTime(token);
				if (remainingTime > 0) {
					tokenBlacklistService.addToBlacklist(token, remainingTime);
					AppLog.debug("토큰이 블랙리스트에 추가됨: " + token);
				}
			}

			// 3. Refresh Token 쿠키 삭제
			clearRefreshTokenCookie();
			
			// 4. 레디스에서 Refresh Token 삭제
        if (userId != null && !userId.isEmpty()) {
            jwtUtil.removeRefreshTokenFromRedis(userId);
        } else {
            // Access Token에서 userId를 못 가져온 경우, 세션에서 시도
            HttpSession session = request.getSession(false);
            if (session != null) {
                String sessionUserId = (String) session.getAttribute("userId");
                if (sessionUserId != null) {
                    jwtUtil.removeRefreshTokenFromRedis(sessionUserId);
                }
            }
        }

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
	 * @param userVo 테넌트유저
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
	 * @param userVo 테넌트유저
	 * @return 단건 조회 결과
	 * @throws Exception
	 */
	@ElService(key = "TNU0001UpdView")
	@RequestMapping(value = "TNU0001UpdView")
	@ElDescription(sub = "테넌트유저 갱신 폼을 위한 조회", desc = "테넌트유저 갱신 폼을 위한 조회를 한다.")
	public UserVo selectUser(UserVo userVo) throws Exception {
		UserVo selectUserVo = userService.selectUser(userVo);

		return selectUserVo;
	}

	/**
	 * 테넌트유저를 등록 처리 한다.
	 *
	 * @param userVo 테넌트유저
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
	 * @param userVo 테넌트유저
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
	 * @param userVo 테넌트유저
	 * @throws Exception
	 */
	@ElService(key = "TNU0002Del")
	@RequestMapping(value = "TNU0002Del")
	@ElDescription(sub = "테넌트유저 삭제처리", desc = "테넌트유저를 삭제 처리한다.")
	public void deleteUser(UserVo userVo) throws Exception {
		userService.deleteUser(userVo);
	}

	private void clearRefreshTokenCookie() {
		try {
			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpServletResponse response = attr.getResponse();
			if (response != null) {
				Cookie cookie = new Cookie("refreshToken", null);
				cookie.setHttpOnly(false);
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

	@ElService(key = "TNU0002selectUserList")
	@RequestMapping(value = "TNU0002selectUserList")
	@ElDescription(sub = "Tenant 사용자 목록 조회", desc = "Tenant별 사용자 목록을 조회합니다.")
	public UserListVo selectUserList(HttpServletRequest request) throws Exception {

		BufferedReader reader = request.getReader();
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		String jsonData = sb.toString();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(jsonData);

		UserVo vo = new UserVo();
		JsonNode voNode = rootNode.get("vo");
		if (voNode != null) {
			vo = mapper.treeToValue(voNode, UserVo.class);
		}

		List<UserVo> resultList = userService.selectUsersByTenant(vo);
		UserListVo returnVo = new UserListVo();
		returnVo.setUserVoList(resultList);

		return returnVo;
	}

	@ElService(key = "saveUserList")
	@RequestMapping(value = "saveUserList")
	@ElDescription(sub = "사용자 목록 저장", desc = "그리드에서 변경된 사용자 데이터를 CUD 처리합니다.")
	public void saveUserList(HttpServletRequest request) throws Exception {
		
		try {
			BufferedReader reader = request.getReader();
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			String jsonData = sb.toString();

			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(jsonData);
			JsonNode saveDataListNode = rootNode.get("saveDataList");

			List<UserVo> userList = new ArrayList<>();

			if (saveDataListNode != null && saveDataListNode.isArray()) {
				for (JsonNode userNode : saveDataListNode) {
					UserVo userVo = new UserVo();

					String userId = userNode.has("userId") ? userNode.get("userId").asText() : "";
					String rowStatus = userNode.has("rowStatus") ? userNode.get("rowStatus").asText() : "";

					userVo.setUserId(userId.isEmpty() ? null : userId);

					if (userNode.has("tenantId")) {
						userVo.setTenantId(userNode.get("tenantId").asText());
					}

					userVo.setName(userNode.get("name").asText());
					userVo.setEmail(userNode.get("email").asText());

					String password = userNode.has("password") ? userNode.get("password").asText() : "";

					if (password != null && !password.isEmpty() && !"KEEP_EXISTING_PASSWORD".equals(password)) {
						String encryptedPassword = passwordEncryptUtil.encryptPassword(password);
						userVo.setPassword(encryptedPassword);
					} else {
						userVo.setPassword(null);
					}

					userVo.setRole(userNode.get("role").asText());
					userVo.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					userVo.setRowStatus(rowStatus);

					userList.add(userVo);
				}

				userService.saveUserList(userList);
				
			}

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 테넌트별 이메일 중복 검사 API
	 */
	@ElService(key = "checkEmailByTenant")
	@RequestMapping(value = "checkEmailByTenant")
	@ElDescription(sub = "테넌트별이메일중복검사", desc = "같은 테넌트 내에서 이메일 중복 확인")
	public Map<String, Object> checkEmailByTenant(UserVo vo, HttpServletRequest request) throws Exception {


		try {
			String email = vo.getEmail();
			String tenantId = vo.getTenantId();
			boolean available = userService.isEmailAvailableInTenant(email, tenantId);
			// Map으로 응답 데이터 반환
			Map<String, Object> response = new HashMap<>();
			response.put("emailCheckResult", available ? "available" : "unavailable");
			response.put("email", email);
			response.put("tenantId", tenantId);	

			return response;

		} catch (Exception e) {
			
			e.printStackTrace();

			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("emailCheckResult", "error");
			errorResponse.put("errorMessage", e.getMessage());

			return errorResponse;
		}
	}

}
