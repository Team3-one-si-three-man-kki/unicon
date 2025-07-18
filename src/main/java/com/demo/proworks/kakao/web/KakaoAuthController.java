package com.demo.proworks.kakao.web;

import com.demo.proworks.kakao.service.KakaoAuthService;
import com.demo.proworks.kakao.vo.KakaoUserInfoVo;
import com.demo.proworks.user.service.UserService;
import com.demo.proworks.user.vo.UserVo;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.log.AppLog;
import com.inswave.elfw.login.LoginAdapter;
import com.inswave.elfw.login.LoginException;
import com.inswave.elfw.login.LoginInfo;
import com.inswave.elfw.login.LoginProcessor;
import com.inswave.elfw.util.ElBeanUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
public class KakaoAuthController {

	@Autowired
	private KakaoAuthService kakaoAuthService;

	private ObjectMapper objectMapper = new ObjectMapper();

	public void setKakaoAuthService(KakaoAuthService kakaoAuthService) {
		this.kakaoAuthService = kakaoAuthService;
	}

	@Resource(name = "loginProcess")
	protected LoginProcessor loginProcess;

	/**
	 * 카카오 로그인 URL 조회
	 */
	@ElService(key = "TNU0000KL")
	@RequestMapping(value = "TNU0000KL")
	@ElDescription(sub = "카카오 로그인 URL 조회", desc = "카카오 로그인 URL을 조회합니다.")
	public ResponseEntity<String> getKakaoLoginUrl(@RequestParam(value = "service") String serviceCode,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			String kakaoLoginUrl = kakaoAuthService.getKakaoLoginUrl(serviceCode);

			Map<String, Object> result = new HashMap<>();
			result.put("success", true);
			result.put("loginUrl", kakaoLoginUrl);
			result.put("message", "카카오 로그인 URL 조회 성공");

			String resultJson = objectMapper.writeValueAsString(result);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resultJson);

		} catch (Exception e) {
			AppLog.error("카카오 로그인 URL 조회 중 오류: " + e.getMessage(), e);

			Map<String, Object> errorResult = new HashMap<>();
			errorResult.put("success", false);
			errorResult.put("message", "카카오 로그인 URL 조회 중 오류가 발생했습니다.");

			try {
				String errorJson = objectMapper.writeValueAsString(errorResult);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
						.body(errorJson);
			} catch (Exception jsonError) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
						.body("{\"success\":false,\"message\":\"서버 오류가 발생했습니다.\"}");
			}
		}
	}

	/**
	 * 카카오 OAuth 콜백 처리
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@ElService(key = "TNU0000KC")
	@RequestMapping(value = "TNU0000KC")
	@ElDescription(sub = "카카오 OAuth 콜백", desc = "카카오 OAuth 콜백을 처리합니다.")
	public ResponseEntity<String> kakaoOAuthCallback(@RequestParam(value = "code", required = false) String code,
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "error_description", required = false) String error_description,
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

		try {
			// 1. 오류 파라미터 확인
			if (error != null) {
				AppLog.error("카카오 OAuth 오류: " + error + " - " + error_description);

				Map<String, Object> errorResult = new HashMap<>();
				errorResult.put("success", false);
				errorResult.put("message", "카카오 로그인이 취소되었거나 오류가 발생했습니다.");
				errorResult.put("error", error);

				// WebSquare용 JSON 응답
				String errorJson = objectMapper.writeValueAsString(errorResult);
				return createJsonResponse(HttpStatus.BAD_REQUEST, errorJson);
			}

			// 2. 인증 코드 확인
			if (code == null || code.isEmpty()) {
				Map<String, Object> errorResult = new HashMap<>();
				errorResult.put("success", false);
				errorResult.put("message", "인증 코드가 없습니다.");

				String errorJson = objectMapper.writeValueAsString(errorResult);
				return createJsonResponse(HttpStatus.BAD_REQUEST, errorJson);
			}

			AppLog.debug("카카오 OAuth 인증 코드 수신: " + code);

			// 3. 카카오 Access Token 교환
			String accessToken = kakaoAuthService.getKakaoAccessToken(code, "TNU0000KC");

			if (accessToken == null) {
				Map<String, Object> errorResult = new HashMap<>();
				errorResult.put("success", false);
				errorResult.put("message", "카카오 Access Token 교환에 실패했습니다.");

				String errorJson = objectMapper.writeValueAsString(errorResult);
				return createJsonResponse(HttpStatus.UNAUTHORIZED, errorJson);
			}
			System.out.println("받아온 access 토큰=============" + accessToken);

			// 4. 카카오 사용자 정보 조회 (VO 반환)
			KakaoUserInfoVo kakaoUserInfo = kakaoAuthService.getKakaoUserInfo(accessToken);
			Map<String, Object> accessresult = new HashMap<>();
			accessresult.put("email", kakaoUserInfo.getEmail());
			accessresult.put("nickname", kakaoUserInfo.getNickname());

			UserService userService = (UserService) ElBeanUtils.getBean("userServiceImpl");

			UserVo existingUser = userService.getUserByEmail((String) accessresult.get("email"));

			if (existingUser != null) {
				String email = existingUser.getEmail();
				String tenantId = existingUser.getTenantId();
				String name = existingUser.getName();
				LoginInfo info = loginProcess.processLogin(request, email, null, tenantId, true);
				AppLog.debug("로그인 처리 결과: " + info);

				// 2. 성공 여부만 판별. 세션 어댑터가 JWT 헤더 설정을 이미 수행함
				if (!info.isSuc()) {
					throw new LoginException("EL.ERROR.LOGIN.0001");
				}

//				Map<String, Object> result = new HashMap<>();
//				result.put("success", true);
//				result.put("userId", existingUser.getUserId());
//				result.put("tenantId", existingUser.getTenantId());
//				result.put("role", existingUser.getRole());
//				result.put("isActive", existingUser.isIsActive());
//				String json = objectMapper.writeValueAsString(result);
				String redirectUrl = "/InsWebApp/websquare/websquare.html?w2xPath=/InsWebApp/main/tenant_user_dashboard.xml"
						+ "&tenant=" + tenantId + "&success=true" + "&userId=" + existingUser.getUserId() + "&userName="
						+ URLEncoder.encode(existingUser.getName(), "UTF-8") + "&role=" + existingUser.getRole()
						+ "&isActive=" + existingUser.isIsActive();

				response.sendRedirect(redirectUrl);
				return null;

			} else {
				// 6. 신규 유저: 추가 정보 입력 페이지로 안내
				// 세션에 Kakao 정보 보관
//				request.getSession().setAttribute("email", accessresult.get("email"));
//				request.getSession().setAttribute("name", accessresult.get("nickname"));
//				// 클라이언트는 이 URL로 리다이렉트하여 테넌트 정보 입력 후 별도 API 호출
//				Map<String, Object> result = new HashMap<>();
//				result.put("success", true);
//				result.put("message", "/signup/kakao-additional");
//				String json = objectMapper.writeValueAsString(result);
//				return ResponseEntity.ok().header("Content-Type", "application/json; charset=UTF-8").body(json);

				// 6. 신규 유저(등록되지 않은 사용자): 안내 후 로그인 페이지로 이동
				AppLog.info("등록되지 않은 카카오 사용자 로그인 시도: " + accessresult.get("email"));

				String redirectUrl = "/InsWebApp/websquare/websquare.html?w2xPath=/InsWebApp/main/unicon_tenant_login_page.xml"
						+ "&error=true" + "&errorType=unregistered" + "&email="
						+ URLEncoder.encode((String) accessresult.get("email"), "UTF-8") + "&message="
						+ URLEncoder.encode("등록되지 않은 사용자입니다. 관리자에게 문의하여 계정을 등록해 주세요.", "UTF-8");

				response.sendRedirect(redirectUrl);
				return null;

			}

		} catch (Exception e) {
			AppLog.error("카카오 OAuth 콜백 처리 중 오류", e);

			// 에러 시에도 WebSquare 페이지로 리다이렉트
			String errorRedirectUrl = "/InsWebApp/websquare/websquare.html?w2xPath=/InsWebApp/main/unicon_tenant_login_page.xml"
					+ "&error=true" + "&message=" + URLEncoder.encode("카카오 로그인 중 오류가 발생했습니다.", "UTF-8");

			try {
				response.sendRedirect(errorRedirectUrl);
			} catch (IOException ioException) {
				AppLog.error("리다이렉트 실패", ioException);
			}
			return null;
		}
	}

	@ElService(key = "TNU0000KSIGN")
	@RequestMapping(value = "TNU0000KSIGN")
	@ElDescription(sub = "카카오 회원가입 콜백", desc = "카카오 OAuth 회원가입 콜백을 처리합니다.")
	public void kakaoSignupCallback(@RequestParam(value = "code", required = false) String code,
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "error_description", required = false) String errorDescription,
			HttpServletRequest request, HttpServletResponse response) throws IOException {

		try {
			// 1. 오류 파라미터 확인
			if (error != null) {
				AppLog.error("카카오 회원가입 OAuth 오류: " + error + " - " + errorDescription);

				String redirectUrl = "/InsWebApp/websquare/websquare.html?w2xPath=/InsWebApp/main/unicon_join.xml"
						+ "&error=true" + "&errorType=kakao_auth_error" + "&message="
						+ URLEncoder.encode("카카오 인증이 취소되었거나 오류가 발생했습니다.", "UTF-8");

				response.sendRedirect(redirectUrl);
				return;
			}

			// 2. 인증 코드 확인
			if (code == null || code.isEmpty()) {
				String redirectUrl = "/InsWebApp/websquare/websquare.html?w2xPath=/InsWebApp/main/unicon_join.xml"
						+ "&error=true" + "&message=" + URLEncoder.encode("인증 코드가 없습니다.", "UTF-8");

				response.sendRedirect(redirectUrl);
				return;
			}

			AppLog.debug("카카오 회원가입 인증 코드 수신: " + code);

			// 3. 카카오 Access Token 교환
			String accessToken = kakaoAuthService.getKakaoAccessToken(code, "TNU0000KSIGN");

			if (accessToken == null) {
				String redirectUrl = "/InsWebApp/websquare/websquare.html?w2xPath=/InsWebApp/main/unicon_join.xml"
						+ "&error=true" + "&message=" + URLEncoder.encode("카카오 Access Token 교환에 실패했습니다.", "UTF-8");

				response.sendRedirect(redirectUrl);
				return;
			}

			System.out.println("카카오 회원가입용 Access Token: " + accessToken);

			// 4. 카카오 사용자 정보 조회 (VO 반환)
			KakaoUserInfoVo kakaoUserInfo = kakaoAuthService.getKakaoUserInfo(accessToken);

			String email = kakaoUserInfo.getEmail();
			String nickname = kakaoUserInfo.getNickname();

			AppLog.info("카카오 회원가입 시도 - 이메일: " + email + ", 닉네임: " + nickname);

			// 5. 기존 사용자 확인
			UserService userService = (UserService) ElBeanUtils.getBean("userServiceImpl");
			UserVo existingUser = userService.getUserByEmail(email);

			if (existingUser != null) {
				// 기존 사용자 - 이미 가입된 유저라고 안내
				AppLog.info("이미 가입된 카카오 사용자: " + email);

				String redirectUrl = "/InsWebApp/websquare/websquare.html?w2xPath=/InsWebApp/main/unicon_join.xml"
						+ "&error=true" + "&errorType=already_registered" + "&email="
						+ URLEncoder.encode(email, "UTF-8") + "&message="
						+ URLEncoder.encode("이미 가입된 사용자입니다. 로그인 페이지에서 카카오 로그인을 이용해주세요.", "UTF-8");

				response.sendRedirect(redirectUrl);
				return;

			} else {
				// 신규 사용자 - 회원가입 페이지로 이동하면서 이메일·이름 전달
				AppLog.info("신규 카카오 사용자 회원가입 진행: " + email);

				String redirectUrl = "/InsWebApp/websquare/websquare.html?w2xPath=/InsWebApp/main/unicon_join.xml"
						+ "&success=true" + "&mode=kakao" + "&email="
						+ URLEncoder.encode(email != null ? email : "", "UTF-8") + "&nickname="
						+ URLEncoder.encode(nickname != null ? nickname : "", "UTF-8") + "&message="
						+ URLEncoder.encode("카카오 인증이 완료되었습니다. 추가 정보를 입력하여 회원가입을 완료해주세요.", "UTF-8");

				response.sendRedirect(redirectUrl);
				return;
			}

		} catch (Exception e) {
			AppLog.error("카카오 회원가입 콜백 처리 중 오류", e);

			try {
				String errorRedirectUrl = "/InsWebApp/websquare/websquare.html?w2xPath=/InsWebApp/main/unicon_join.xml"
						+ "&error=true" + "&message=" + URLEncoder.encode("카카오 회원가입 처리 중 서버 오류가 발생했습니다.", "UTF-8");

				response.sendRedirect(errorRedirectUrl);
			} catch (IOException ioException) {
				AppLog.error("에러 리다이렉트 실패", ioException);
			}
		}
	}

	private ResponseEntity<String> createErrorResponse(HttpStatus status, String message) {
		try {
			Map<String, Object> err = new HashMap<>();
			err.put("success", false);
			err.put("message", message);
			String json = objectMapper.writeValueAsString(err);

			// >>> 변경된 부분: Content-Type 헤더 일관성 유지
			return ResponseEntity.status(status).header("Content-Type", "application/json; charset=UTF-8").body(json);

		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.header("Content-Type", "application/json; charset=UTF-8")
					.body("{\"success\":false,\"message\":\"알 수 없는 오류\"}");
		}
	}

	/**
	 * 카카오 로그인 상태 확인
	 */
	@ElService(key = "TNU0000KS")
	@RequestMapping(value = "TNU0000KS")
	@ElDescription(sub = "카카오 로그인 상태 확인", desc = "현재 카카오 로그인 상태를 확인합니다.")
	public ResponseEntity<String> checkKakaoLoginStatus(HttpServletRequest request, HttpServletResponse response) {
		try {
			// JWT 인증 상태 확인
			Boolean isAuthenticated = (Boolean) request.getAttribute("jwtAuthenticated");
			String userId = (String) request.getAttribute("userId");

			Map<String, Object> result = new HashMap<>();

			if (isAuthenticated != null && isAuthenticated && userId != null) {
				result.put("success", true);
				result.put("authenticated", true);
				result.put("userId", userId);
				result.put("message", "로그인 상태입니다.");
			} else {
				result.put("success", true);
				result.put("authenticated", false);
				result.put("message", "로그인되지 않은 상태입니다.");
			}

			String resultJson = objectMapper.writeValueAsString(result);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resultJson);

		} catch (Exception e) {
			AppLog.error("카카오 로그인 상태 확인 중 오류: " + e.getMessage(), e);

			Map<String, Object> errorResult = new HashMap<>();
			errorResult.put("success", false);
			errorResult.put("message", "로그인 상태 확인 중 오류가 발생했습니다.");

			try {
				String errorJson = objectMapper.writeValueAsString(errorResult);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
						.body(errorJson);
			} catch (Exception jsonError) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
						.body("{\"success\":false,\"message\":\"서버 오류가 발생했습니다.\"}");
			}
		}
	}

	/**
	 * 카카오 Access Token으로 사용자 정보 조회
	 */
	private Map<String, Object> getKakaoUserInfo(String accessToken) {
		try {
			// 카카오 사용자 정보 요청 URL
			String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

			// HTTP 헤더 설정
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + accessToken);
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> request = new HttpEntity<>(headers);

			// 카카오 API 호출
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, Map.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				Map<String, Object> responseBody = response.getBody();

				// 카카오 사용자 정보 추출
				Map<String, Object> userInfo = new HashMap<>();
				userInfo.put("kakaoId", responseBody.get("id"));

				Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
				if (kakaoAccount != null) {
					userInfo.put("email", kakaoAccount.get("email"));

					Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
					if (profile != null) {
						userInfo.put("nickname", profile.get("nickname"));
						userInfo.put("profileImage", profile.get("profile_image_url"));
					}
				}

				return userInfo;
			}

			AppLog.error("카카오 사용자 정보 조회 실패: " + response.getStatusCode());
			return null;

		} catch (Exception e) {
			AppLog.error("카카오 사용자 정보 조회 중 오류: " + e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 카카오 사용자 정보로 시스템 로그인 처리
	 */
	private Map<String, Object> processSystemLogin(Map<String, Object> kakaoUserInfo) {

		System.out.println("시스템로그인 함수 + ====" + kakaoUserInfo);
		String kakaoId = String.valueOf(kakaoUserInfo.get("kakaoId"));
		String email = (String) kakaoUserInfo.get("email");
		String nickname = (String) kakaoUserInfo.get("nickname");

		// 기존 회원 조회 또는 신규 회원 생성
		// 여기서는 예시로 간단히 처리
//			Map<String, Object> userInfo = findOrCreateUser(kakaoId, email, nickname);
//
//			if (userInfo != null) {
//				// JWT 토큰 생성
//				String accessToken = generateJwtToken(userInfo);
//				String refreshToken = generateRefreshToken(userInfo);
//
//				Map<String, Object> result = new HashMap<>();
//				result.put("success", true);
//				result.put("message", "카카오 로그인 성공");
//				result.put("accessToken", accessToken);
//				result.put("refreshToken", refreshToken);
//				result.put("userInfo", userInfo);
//
//				return result;
//			} else {
//				Map<String, Object> result = new HashMap<>();
//				result.put("success", false);
//				result.put("message", "사용자 정보 처리 실패");
//				return result;
//			}
//
//		} catch (Exception e) {
//			AppLog.error("시스템 로그인 처리 중 오류: " + e.getMessage(), e);
//
//			Map<String, Object> result = new HashMap<>();
//			result.put("success", false);
//			result.put("message", "로그인 처리 중 오류가 발생했습니다.");
//			return result;
//		}
		return null;
	}

	/**
	 * WebSquare용 JSON 응답 생성
	 */
	private ResponseEntity<String> createJsonResponse(HttpStatus status, String jsonBody) {
		return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
				.header("Access-Control-Allow-Headers", "Content-Type, Authorization").body(jsonBody);
	}

	/**
	 * Refresh Token을 HttpOnly 쿠키에 설정
	 */
	private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setSecure(true); // HTTPS 환경에서만
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
		response.addCookie(refreshTokenCookie);
	}

}