package com.demo.proworks.jwt;

import com.demo.proworks.jwt.JwtUtil;
import com.demo.proworks.user.service.UserService;
import com.demo.proworks.user.vo.UserVo;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.log.AppLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElValidator;

@Controller
public class TokenRefreshController {

	@Autowired
	private JwtUtil jwtUtil;

	/** UserService */
	@Resource(name = "userServiceImpl")
	private UserService userService;

	@ElService(key = "TNU0000TR")
	@RequestMapping(value = "TNU0000TR")
	@ElDescription(sub = "토큰 재발행", desc = "토큰 재발행")
	ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 쿠키에서 Refresh Token 추출
			String refreshToken = getRefreshTokenFromCookie(request);
			if (refreshToken == null || refreshToken.isEmpty()) {
				String errJson = "{\"success\":false,\"message\":\"Refresh Token이 없습니다.\"}";
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON)
						.body(errJson);
			}

			// 2. Refresh Token 유효성 검증
			if (!jwtUtil.validateToken(refreshToken)) {
				String errJson = "{\"success\":false,\"message\":\"Refresh Token이 유효하지 않습니다.\"}";
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON)
						.body(errJson);
			}

			// 3. Refresh Token에서 사용자 정보 추출
			String userId = jwtUtil.getUserIdFromToken(refreshToken);
			UserVo user = userService.getUserByEmail(userId);

			// 4. 새로운 Access Token 생성
			String newAccessToken = jwtUtil.generateAccessToken(userId, user.getTenantId(), user.getRole(),
					user.isIsActive());
			// 5. 새로운 Refresh Token 생성 (선택)
			String newRefreshToken = jwtUtil.generateRefreshToken(userId);

			// 6. 응답 헤더에 새로운 Access Token 설정
			response.setHeader("Authorization", "Bearer " + newAccessToken);
			response.setHeader("Access-Control-Expose-Headers", "Authorization");
			// 7. 쿠키에 Refresh Token 설정
			setRefreshTokenCookie(response, newRefreshToken);

			// 8. JSON 문자열로 결과 반환
			String successJson = String.format(
					"{\"success\":true,\"message\":\"토큰이 성공적으로 재발급되었습니다.\",\"accessToken\":\"%s\"}", newAccessToken);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(successJson);

		} catch (Exception e) {
			String errorJson = "{\"success\":false,\"message\":\"토큰 재발급 중 오류가 발생했습니다.\"}";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
					.body(errorJson);
		}
	}

	/**
	 * 쿠키에서 Refresh Token 추출
	 */
	private String getRefreshTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("refreshToken".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * Refresh Token 쿠키 설정
	 */
	private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
		Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
		refreshCookie.setHttpOnly(false);
		refreshCookie.setSecure(false);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(7 * 24 * 60 * 60); 
		response.addCookie(refreshCookie);
	}

}
