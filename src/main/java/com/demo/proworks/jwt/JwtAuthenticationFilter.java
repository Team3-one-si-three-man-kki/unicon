package com.demo.proworks.jwt;

import com.demo.proworks.cmmn.ProworksUserHeader;
import com.demo.proworks.user.service.UserService;
import com.demo.proworks.user.vo.UserVo;
import com.inswave.elfw.util.ElBeanUtils;
import com.inswave.elfw.intercept.ElServletFilter;
import com.inswave.elfw.log.AppLog;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends ElServletFilter {

	private JwtUtil jwtUtil;

	public void setJwtUtil(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig); // 부모 클래스의 초기화 호출
		AppLog.debug("JWT Authentication Filter 초기화");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		AppLog.debug("=== JWT HttpServletRequest 진입: " + request);
		AppLog.debug("=== JWT Filter 진입: " + httpRequest.getRequestURI());
//		AppLog.debug("-> jwtAuthenticated=" + httpRequest.getAttribute("jwtAuthenticated"));

		// GUEST 서비스인지 확인
//        if (isGuestService(httpRequest)) {
//            // GUEST 서비스는 JWT 인증 없이 통과
//            httpRequest.setAttribute("jwtAuthenticated", false);
//            AppLog.debug("GUEST 서비스 - JWT 인증 제외: " + httpRequest.getRequestURI());
//            chain.doFilter(request, response);
//            return;
//        }

		// 나머지 JWT 인증 로직...
		String token = extractToken(httpRequest);

		if (token != null && !token.isEmpty() && jwtUtil.validateToken(token)) {
			// JWT 인증 성공 로직
			// ... 기존 코드 유지
			try {
				// JWT 토큰에서 클레임 추출
				String userId = jwtUtil.getUserIdFromToken(token);
				String tenantId = jwtUtil.getTenantIdFromToken(token);
				String userRole = jwtUtil.getRoleFromToken(token);
				Boolean isActive = jwtUtil.getIsActiveFromToken(token);

				// request 속성에 JWT 클레임 정보 설정
				httpRequest.setAttribute("jwtAuthenticated", true);
				httpRequest.setAttribute("userId", userId);
				httpRequest.setAttribute("tenantId", tenantId);
				httpRequest.setAttribute("userRole", userRole);
				httpRequest.setAttribute("isActive", isActive);

				AppLog.debug("JWT 인증 성공 - userId: " + userId + ", role: " + userRole + ", tenantId : " + tenantId
						+ ", isActive : " + isActive);

			} catch (Exception e) {
				AppLog.error("JWT 토큰 처리 중 오류: " + e.getMessage(), e);
				httpRequest.setAttribute("jwtAuthenticated", false);
			}
		} else {
			// JWT 토큰이 없거나 유효하지 않은 경우
			httpRequest.setAttribute("jwtAuthenticated", false);
		}

		chain.doFilter(request, response);
	}

	private boolean isGuestService(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();

		// Context Path 제거
		String svcPath = requestURI.substring(contextPath.length());

		// 확장자 제거하여 서비스 ID 추출
		String svcId = svcPath;
		if (svcPath.contains(".")) {
			svcId = svcPath.substring(0, svcPath.lastIndexOf("."));
		}
		if (svcId.startsWith("/")) {
			svcId = svcId.substring(1);
		}

		// GUEST 서비스 판단 로직 (ProworksAuthProcess와 동일한 로직 사용)
		String requiredRole = getRequiredRoleForService(svcId);
		return "GUEST".equals(requiredRole);
	}

	private String getRequiredRoleForService(String svcId) {
		// ProworksAuthProcess의 getRequiredRoleForProWorksService와 동일한 로직
		if (svcId.startsWith("TNU0003"))
			return "ADMIN";
		if (svcId.startsWith("TNU0002"))
			return "MANAGER";
		if (svcId.startsWith("TNU0001"))
			return "USER";
		return "GUEST";
	}

	private String extractToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		AppLog.debug("Authorization Header: " + bearerToken); // 추가
		if (bearerToken != null && !bearerToken.isEmpty() && bearerToken.startsWith("Bearer ")) {
			String token = bearerToken.substring(7);
			AppLog.debug("Extracted Token: " + token); // 추가
			return token;
		}
		return null;
	}

	@Override
	public void destroy() {
		super.destroy(); // 부모 클래스의 소멸자 호출
		AppLog.debug("JWT Authentication Filter 소멸");
	}
}
