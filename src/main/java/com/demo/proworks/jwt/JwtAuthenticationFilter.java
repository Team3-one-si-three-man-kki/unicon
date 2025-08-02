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
	
    private TokenBlacklistService tokenBlacklistService;
    
  
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    public void setTokenBlacklistService(TokenBlacklistService tokenBlacklistService) {
        this.tokenBlacklistService = tokenBlacklistService;
    }

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig); // 부모 클래스의 초기화 호출
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
	
		// 나머지 JWT 인증 로직...
		String token = extractToken(httpRequest);


		if (token != null && !token.isEmpty()) {
			// 1. 블랙리스트 확인
			if (tokenBlacklistService.isBlacklisted(token)) {
				httpRequest.setAttribute("jwtAuthenticated", false);
				chain.doFilter(request, response);
				return;
			}

			// 2. 토큰 유효성 검증
			if (jwtUtil.validateToken(token)) {
				try {
					// 기존 토큰 처리 로직
					String userId = jwtUtil.getUserIdFromToken(token);
					String tenantId = jwtUtil.getTenantIdFromToken(token);
					String userRole = jwtUtil.getRoleFromToken(token);
					Boolean isActive = jwtUtil.getIsActiveFromToken(token);

					httpRequest.setAttribute("jwtAuthenticated", true);
					httpRequest.setAttribute("userId", userId);
					httpRequest.setAttribute("tenantId", tenantId);
					httpRequest.setAttribute("userRole", userRole);
					httpRequest.setAttribute("isActive", isActive);

				} catch (Exception e) {
					httpRequest.setAttribute("jwtAuthenticated", false);
				}
			} else {
				httpRequest.setAttribute("jwtAuthenticated", false);
			}
		} else {
			httpRequest.setAttribute("jwtAuthenticated", false);
		}

		chain.doFilter(request, response);
	}


	private String extractToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && !bearerToken.isEmpty() && bearerToken.startsWith("Bearer ")) {
			String token = bearerToken.substring(7);
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
