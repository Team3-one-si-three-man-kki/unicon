package com.demo.proworks.jwt;


import com.demo.proworks.user.service.UserService;
import com.demo.proworks.user.vo.UserVo;
import com.inswave.elfw.annotation.ElService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;

import com.inswave.elfw.annotation.ElDescription;

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

			// 2. JWT에서 userId 추출 (레디스 확인을 위해)
			String userId = jwtUtil.getUserIdFromToken(refreshToken);
			if (userId == null || userId.isEmpty()) {
				String errJson = "{\"success\":false,\"message\":\"Refresh Token에서 사용자 정보를 추출할 수 없습니다.\"}";
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON)
						.body(errJson);
			}

			// 3. 레디스에서 Refresh Token 존재 여부 확인
			if (!jwtUtil.isRefreshTokenExistsInRedis(userId, refreshToken)) {
				String errJson = "{\"success\":false,\"message\":\"유효하지 않은 Refresh Token입니다.\"}";
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON)
						.body(errJson);
			}

			// 4. Refresh Token 유효성 검증
			if (!jwtUtil.validateToken(refreshToken)) {
				String errJson = "{\"success\":false,\"message\":\"Refresh Token이 유효하지 않습니다.\"}";
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON)
						.body(errJson);
			}

			// 5. Refresh Token에서 사용자 정보 추출
			userId = jwtUtil.getUserIdFromToken(refreshToken);
			UserVo user = userService.getUserByEmail(userId);

			// 6. 새로운 Access Token 생성
			String newAccessToken = jwtUtil.generateAccessToken(userId, user.getTenantId(), user.getRole(),
					user.isIsActive());

			// 7. 응답 헤더에 새로운 Access Token 설정
			response.setHeader("Authorization", "Bearer " + newAccessToken);
			response.setHeader("Access-Control-Expose-Headers", "Authorization");

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

}
