package com.demo.proworks.jwt;

import com.demo.proworks.jwt.JwtUtil;
import com.demo.proworks.user.service.UserService;
import com.demo.proworks.user.vo.UserVo;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.log.AppLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
	public ResponseEntity<Map<String, Object>> refreshToken(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> result = new HashMap<>();

		try {
			// 1. 쿠키에서 Refresh Token 추출
			String refreshToken = getRefreshTokenFromCookie(request);
			System.out.println("refreshToken==" + refreshToken);

			if (refreshToken == null || refreshToken.isEmpty()) {
				result.put("success", false);
				result.put("message", "Refresh Token이 없습니다.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
			}

			// 2. Refresh Token 유효성 검증
			if (!jwtUtil.validateToken(refreshToken)) {
				result.put("success", false);
				result.put("message", "Refresh Token이 유효하지 않습니다.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
			}

			// 3. Refresh Token에서 사용자 정보 추출
			String userId = jwtUtil.getUserIdFromToken(refreshToken);

			// 4. 사용자 정보로 새로운 Access Token 생성
			UserVo user = userService.getUserByEmail(userId);
			System.out.println("user==" + user);

			// 실제로는 데이터베이스에서 사용자 정보를 다시 조회해야 함
			String newAccessToken = jwtUtil.generateAccessToken(userId, user.getTenantId(), // 실제로는 DB에서 조회
					user.getRole(), // 실제로는 DB에서 조회
					user.isIsActive() // 실제로는 DB에서 조회
			);

			// 5. 새로운 Refresh Token도 생성 (선택사항)
			String newRefreshToken = jwtUtil.generateRefreshToken(userId);

			// 6. 응답 헤더에 새로운 Access Token 설정
			response.setHeader("Authorization", "Bearer " + newAccessToken);
			response.setHeader("Access-Control-Expose-Headers", "Authorization");

			// 7. 새로운 Refresh Token을 쿠키로 설정
			setRefreshTokenCookie(response, newRefreshToken);

			result.put("success", true);
			result.put("message", "토큰이 성공적으로 재발급되었습니다.");
			result.put("accessToken", newAccessToken);

			AppLog.debug("토큰 재발급 성공 - userId: " + userId);
			return ResponseEntity.ok(result);

		} catch (Exception e) {
			AppLog.error("토큰 재발급 중 오류: " + e.getMessage(), e);
			result.put("success", false);
			result.put("message", "토큰 재발급 중 오류가 발생했습니다.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
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
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(false); // 개발 시에는 false
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
		response.addCookie(refreshCookie);
	}

}
