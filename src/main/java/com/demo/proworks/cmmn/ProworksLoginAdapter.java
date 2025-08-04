package com.demo.proworks.cmmn;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.inswave.elfw.exception.ElException;
import com.inswave.elfw.log.AppLog;
import com.inswave.elfw.login.LoginAdapter;
import com.inswave.elfw.login.LoginException;
import com.inswave.elfw.login.LoginInfo;
import com.inswave.elfw.util.ElBeanUtils;

import com.demo.proworks.jwt.JwtUtil;
import com.demo.proworks.jwt.TokenBlacklistService;
import com.demo.proworks.user.service.UserService;
import com.demo.proworks.user.vo.UserVo;
import com.demo.proworks.util.PasswordEncryptUtil;

/**
 * @subject : ProworksLoginAdapter.java
 * @description : 프로젝트 로그인 어댑터
 * @author : 개발팀
 * @since : 2025/05/19
 * @modification ===========================================================
 *               DATE AUTHOR NOTE
 *               ===========================================================
 *               2025/05/19 샘플개발팀 최초 생성
 * 
 */

public class ProworksLoginAdapter extends LoginAdapter {

// 상수 정의 (클래스 상단에 추가)
	private static final String REFRESH_TOKEN_PREFIX = "refresh:";

	/**
	 * 데모용 로그인 어댑터의 생성자
	 * 
	 * @param adapterInfoMap Adapter 정보
	 */
	public ProworksLoginAdapter(Map<String, Object> adapterInfoMap) {
		super(adapterInfoMap);
	}


	public void addRefreshTokenToRedis(String userId, String refreshToken, long expirationTime) {
		try {
			RedisTemplate<String, Object> redisTemplate = (RedisTemplate<String, Object>) ElBeanUtils
					.getBean("redisTemplate");

			if (redisTemplate != null) {
				String key = REFRESH_TOKEN_PREFIX + userId;
				redisTemplate.opsForValue().set(key, refreshToken, expirationTime, TimeUnit.SECONDS);
				AppLog.debug("Redis에 리프레쉬 토큰 저장 성공: " + key);
			} else {
				AppLog.error("redisTemplate이 null입니다!");
			}
		} catch (Exception e) {
			AppLog.error("Redis에 리프레쉬 토큰 저장 중 오류: " + e.getMessage(), e);
		}
	}

	/**
	 * 데모용 로그인 처리를 담당하는 구현체 메소드. 프레임워크 DefaultLoginAdapter 추상클래스의 로그인 구현체 메소드
	 * 
	 * @param request
	 * @param id
	 * @param params  기타 동적 파라미터에 추가할 수 있다.(ex. 서비스 구현체 )
	 * @return LoginInfo
	 * @throws LoginException
	 */
	@Override
	public LoginInfo login(HttpServletRequest request, String id, Object... params) throws LoginException {

		try {
			// 파라미터 추출
			String pw = (String) params[0];
			String subDomain = (String) params[1];
			Boolean isSocial = params.length > 2 && Boolean.TRUE.equals(params[2]);

			// 서비스 및 유틸리티 빈 획득
			UserService userService = (UserService) ElBeanUtils.getBean("userServiceImpl");
			PasswordEncryptUtil passwordEncryptUtil = (PasswordEncryptUtil) ElBeanUtils.getBean("passwordEncryptUtil");

			// 빈 null 체크
			if (userService == null) {
				AppLog.error("userService가 null입니다!");
				throw new LoginException("EL.ERROR.LOGIN.0003");
			}

			if (passwordEncryptUtil == null) {
				AppLog.error("passwordEncryptUtil이 null입니다!");
				throw new LoginException("EL.ERROR.LOGIN.0003");
			}

			// 사용자 정보 조회
			UserVo userVo = new UserVo();
			userVo.setEmail(id);
			userVo.setSubDomain(subDomain);
			UserVo loginUser = userService.loginUser(userVo);

			// 사용자 존재 여부 확인
			if (loginUser == null) {
				throw new LoginException("EL.ERROR.LOGIN.0001");
			}

			// 비밀번호 검증
			if (!isSocial) {
				String dbPw = loginUser.getPassword();
				if (pw == null || !passwordEncryptUtil.verifyPassword(pw, dbPw)) {
					throw new LoginException("EL.ERROR.LOGIN.0002");
				}
			}

			// JWT 토큰 생성
			JwtUtil jwtUtil = (JwtUtil) ElBeanUtils.getBean("jwtUtil");
			String accessToken = jwtUtil.generateAccessToken(id, loginUser.getTenantId(), loginUser.getRole(),
					loginUser.isIsActive());
			String refreshToken = jwtUtil.generateRefreshToken(id);

			// Access Token은 응답 헤더에 설정 (기존과 동일)
			setAccessTokenToResponse(accessToken);

			// Refresh Token은 HttpOnly 쿠키로 설정
			setRefreshTokenCookie(refreshToken);

			// Refresh Token을 레디스에 저장
			long refreshTokenExpirationMillis = jwtUtil.getRefreshTokenExpiration();
			long refreshTokenExpirationSeconds = refreshTokenExpirationMillis / 1000;
			addRefreshTokenToRedis(id, refreshToken, refreshTokenExpirationSeconds);

			// ElHeader에 userId 설정
			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpServletRequest currentRequest = attr.getRequest();

			// 세션에 userId 설정
			HttpSession session = currentRequest.getSession();
			session.setAttribute("userId", id);

			// 로그인 성공 정보 설정
			LoginInfo info = new LoginInfo();
			info.setSuc(true);

			return info;

		} catch (NumberFormatException e) {
			AppLog.error("login Error - 숫자 형변환 오류", e);
			throw new LoginException("EL.ERROR.LOGIN.0001");
		} catch (ElException e) {
			AppLog.error("login Error - ElException", e);
			throw e;
		} catch (Exception e) {
			AppLog.error("login Error - 예기치 않은 오류", e);
			throw new LoginException("EL.ERROR.LOGIN.0003");
		}
	}

	/**
	 * 응답 헤더에 JWT 토큰을 설정하는 메소드
	 * 
	 * @param accessToken  액세스 토큰
	 * @param refreshToken 리프레시 토큰
	 */

	/**
	 * Access Token을 응답 헤더에 설정
	 */
	private void setAccessTokenToResponse(String accessToken) {
		try {
			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpServletResponse response = attr.getResponse();
			if (response != null) {
				response.setHeader("Authorization", "Bearer " + accessToken);
				response.setHeader("Access-Control-Expose-Headers", "Authorization");
				response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
				response.setHeader("Access-Control-Allow-Origin", "*");
			}
		} catch (Exception e) {
			AppLog.error("Access Token 헤더 설정 중 오류: " + e.getMessage(), e);
		}
	}

	/**
	 * Refresh Token을 HttpOnly 쿠키로 설정
	 */
	private void setRefreshTokenCookie(String refreshToken) {
		try {
			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpServletResponse response = attr.getResponse();
			if (response != null) {
				Cookie refreshCookie = new Cookie("refreshToken", refreshToken);

				// 쿠키 보안 설정
				refreshCookie.setHttpOnly(false); // JavaScript 접근 차단
				refreshCookie.setSecure(true); // HTTPS에서만 전송 (개발 시에는 false)
				refreshCookie.setPath("/"); // 모든 경로에서 사용
				refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일 (Refresh Token 만료시간과 동일하게)
				response.addCookie(refreshCookie);
				AppLog.debug("Refresh Token 쿠키 설정 완료");
			}
		} catch (Exception e) {
			AppLog.error("Refresh Token 쿠키 설정 중 오류: " + e.getMessage(), e);
		}
	}

	/**
	 * 데모용 로그아웃 처리를 담당하는 구현체 메소드. 프레임워크 DefaultLoginAdapter 추상클래스의 로그아웃 구현체 메소드
	 * 
	 * @param request
	 * @param id
	 * @param params  기타 동적 파라미터에 추가할 수 있다.
	 * @return LoginInfo
	 * @throws LoginException
	 */
	@Override
	public LoginInfo logout(HttpServletRequest request, String id, Object... params) throws LoginException {
		LoginInfo info = new LoginInfo();
		try {
			// 1. Authorization 헤더에서 토큰 추출
			String authHeader = request.getHeader("Authorization");
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = authHeader.substring(7);

				// 2. 토큰을 블랙리스트에 추가
				TokenBlacklistService blacklistService = (TokenBlacklistService) ElBeanUtils
						.getBean("tokenBlacklistService");

				if (blacklistService != null) {
					long remainingTime = blacklistService.getTokenRemainingTime(token);
					if (remainingTime > 0) {
						blacklistService.addToBlacklist(token, remainingTime);
					}
				}
			}
			// 3. 로그아웃 성공 설정
			info.setSuc(true);
		} catch (Exception e) {
			AppLog.error("로그아웃 중 오류: " + e.getMessage(), e);
			throw new LoginException(e);
		}

		return info;
	}
}