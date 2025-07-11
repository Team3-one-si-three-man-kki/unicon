package com.demo.proworks.cmmn;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.inswave.elfw.exception.ElException;
import com.inswave.elfw.log.AppLog;
import com.inswave.elfw.login.LoginAdapter;
import com.inswave.elfw.login.LoginException;
import com.inswave.elfw.login.LoginInfo;
import com.inswave.elfw.util.ElBeanUtils;

import com.demo.proworks.jwt.JwtUtil;
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

	/**
	 * 데모용 로그인 어댑터의 생성자
	 * 
	 * @param adapterInfoMap Adapter 정보
	 */
	public ProworksLoginAdapter(Map<String, Object> adapterInfoMap) {
		super(adapterInfoMap);
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
			String tenantId = (String) params[1];

			AppLog.debug("로그인 시도 - 테넌트 ID: " + tenantId);

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
			userVo.setTenantId(tenantId);

			AppLog.debug("사용자 조회 시도 - UserVo: " + userVo);

			UserVo loginUser = userService.loginUser(userVo);
			AppLog.debug("사용자 조회 결과 - LoginUser: " + loginUser);

			// 사용자 존재 여부 확인
			if (loginUser == null) {
				throw new LoginException("EL.ERROR.LOGIN.0001");
			}

			// 비밀번호 검증
			String dbPw = loginUser.getPassword();
			if (pw == null || !passwordEncryptUtil.verifyPassword(pw, dbPw)) {
				throw new LoginException("EL.ERROR.LOGIN.0002");
			}

			// JWT 토큰 생성
			JwtUtil jwtUtil = (JwtUtil) ElBeanUtils.getBean("jwtUtil");
			String accessToken = jwtUtil.generateAccessToken(id, tenantId, loginUser.getRole(), loginUser.isIsActive());
			String refreshToken = jwtUtil.generateRefreshToken(id);

			// 응답 헤더에 토큰 추가
			setTokensToResponse(accessToken, refreshToken);
			// ElHeader에 userId 설정
			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpServletRequest currentRequest = attr.getRequest();

			// 세션에 userId 설정
			HttpSession session = currentRequest.getSession();
			session.setAttribute("userId", id);

			// 로그인 성공 정보 설정
			LoginInfo info = new LoginInfo();
			info.setSuc(true);

			AppLog.debug("[Login] Proworks Login 성공 with JWT - userId: " + id);
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
	private void setTokensToResponse(String accessToken, String refreshToken) {
		try {
			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpServletResponse response = attr.getResponse();
			if (response != null) {
				// Bearer 접두사 포함하여 설정
				response.setHeader("Authorization", "Bearer " + accessToken);
				response.setHeader("Refresh-Token", refreshToken);

				// CORS 설정 - 중요!
				response.setHeader("Access-Control-Expose-Headers", "Authorization, Refresh-Token");
				response.setHeader("Access-Control-Allow-Headers", "Authorization, Refresh-Token, Content-Type");
				response.setHeader("Access-Control-Allow-Origin", "*");

				AppLog.debug("토큰 헤더 설정 완료 - Authorization: Bearer " + accessToken);
			}
		} catch (Exception e) {
			AppLog.error("응답 헤더 설정 중 오류: " + e.getMessage(), e);
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
			// 1. 로그아웃 처리로직 추가

			// 2. 로그아웃 성공 설정
			info.setSuc(true);
			AppLog.debug("[Logout] Proworks Logout 성공.....");

		} catch (Exception e) {
			throw new LoginException(e);
		}
		return info;
	}
}