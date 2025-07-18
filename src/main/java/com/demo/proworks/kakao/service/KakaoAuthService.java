package com.demo.proworks.kakao.service;

import com.demo.proworks.kakao.vo.KakaoUserInfoVo;
import com.demo.proworks.user.service.UserService;
import com.demo.proworks.user.vo.UserVo;
import com.demo.proworks.jwt.JwtUtil;
import com.inswave.elfw.log.AppLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoAuthService {

    private String kakaoClientId;
    private String kakaoClientSecret;
    private String kakaoRedirectUri;

    private final String kakaoTokenUrl    = "https://kauth.kakao.com/oauth/token";
    private final String kakaoUserInfoUrl = "https://kapi.kakao.com/v2/user/me";

    @Autowired
    private JwtUtil jwtUtil;

    @Resource(name = "userServiceImpl")
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    // XML 또는 @Value 주입용 setter
    public void setKakaoClientId(String kakaoClientId) {
        this.kakaoClientId = kakaoClientId;
    }

    public void setKakaoClientSecret(String kakaoClientSecret) {
        this.kakaoClientSecret = kakaoClientSecret;
    }

    public void setKakaoRedirectUri(String kakaoRedirectUri) {
        this.kakaoRedirectUri = kakaoRedirectUri;
    }

    /**
     * 카카오 로그인 URL 생성
     */
    public String getKakaoLoginUrl(String serviceCode) {
        String redirectUri= kakaoRedirectUri +"/"+serviceCode+".pwkjson";
        return "https://kauth.kakao.com/oauth/authorize?" +
               "client_id=" + kakaoClientId +
               "&redirect_uri=" + redirectUri +
               "&response_type=code";
    }
    
   

    /**
     * Authorization Code → Access Token 교환
     */
    private String exchangeCodeForAccessToken(String code, String serviceCode) {
    String redirectUri = kakaoRedirectUri + "/" + serviceCode + ".pwkjson";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type",    "authorization_code");
        params.add("client_id",     kakaoClientId);
        params.add("redirect_uri",  redirectUri);
        params.add("code",          code);
        params.add("client_secret", kakaoClientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        System.out.println("headers = " + headers);
        System.out.println("access토큰 가져오기 코드로"+params);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        System.out.println("request=========="+request);
        ResponseEntity<Map> response = restTemplate.postForEntity(kakaoTokenUrl, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = response.getBody();
            return (body != null ? (String) body.get("access_token") : null);
        }

        AppLog.error("카카오 Access Token 교환 실패: HTTP " + response.getStatusCode());
        return null;
    }

    /**
     * 카카오 액세스 토큰 획득
     */
    public String getKakaoAccessToken(String code, String serviceCode) {
    	System.out.println("서비스 받아온 코드"+code);
        String token = exchangeCodeForAccessToken(code, serviceCode);
        if (token == null) {
            throw new RuntimeException("카카오 토큰 교환에 실패했습니다.");
        }
        return token;
    }

    /**
     * 카카오 사용자 정보 조회
     */
    public KakaoUserInfoVo getKakaoUserInfo(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        System.out.println("headers====="+headers);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
            kakaoUserInfoUrl, HttpMethod.GET, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            @SuppressWarnings("unchecked")
            Map<String, Object> respBody = response.getBody();
            return parseKakaoUserInfo(respBody);
        }

        throw new RuntimeException("카카오 사용자 정보 조회 실패: HTTP " + response.getStatusCode());
    }

    /**
     * 응답 맵 → KakaoUserInfoVo 변환
     */
    @SuppressWarnings("unchecked")
    private KakaoUserInfoVo parseKakaoUserInfo(Map<String, Object> response) {
        KakaoUserInfoVo vo = new KakaoUserInfoVo();
        vo.setId(String.valueOf(response.get("id")));

        Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");
        if (kakaoAccount != null) {
            vo.setEmail((String) kakaoAccount.get("email"));
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile != null) {
                vo.setNickname((String) profile.get("nickname"));
            }
        }
        return vo;
    }

    /**
     * 카카오 로그인 처리 (로그인 또는 회원가입)
     */
//    public Map<String, Object> processKakaoLogin(String code) throws Exception {
//        Map<String, Object> result = new HashMap<>();
//        try {
//            // 1. 액세스 토큰
//            String kakaoAccessToken = getKakaoAccessToken(code);
//            AppLog.debug("카카오 액세스 토큰 획득 성공");
//
//            // 2. 사용자 정보
//            KakaoUserInfoVo userInfo = getKakaoUserInfo(kakaoAccessToken);
//            AppLog.debug("카카오 사용자 정보 조회 성공: " + userInfo.getEmail());
//
//            // 3. 기존 회원 조회
//            UserVo existing = userService.getUserByEmail(userInfo.getEmail());
//
//            UserVo user;
//            boolean isNewUser = false;
//            if (existing == null) {
//                user = createNewKakaoUser(userInfo);
//                userService.insertUser(user);
//                isNewUser = true;
//                AppLog.debug("신규 카카오 사용자 생성: " + user.getEmail());
//            } else {
//                user = updateKakaoUserInfo(existing, userInfo);
//                userService.updateUser(user);
//                AppLog.debug("기존 사용자 정보 업데이트: " + user.getEmail());
//            }
//
//            // 4. JWT 생성
//            String accessToken  = jwtUtil.generateAccessToken(
//                user.getEmail(), user.getTenantId(), user.getRole(), user.isIsActive());
//            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
//
//            // 5. 결과
//            result.put("success",      true);
//            result.put("accessToken",  accessToken);
//            result.put("refreshToken", refreshToken);
//            result.put("user",         user);
//            result.put("isNewUser",    isNewUser);
//            result.put("message",      isNewUser ? "카카오 회원가입 완료" : "카카오 로그인 완료");
//
//        } catch (Exception e) {
//            AppLog.error("카카오 로그인 처리 중 오류: " + e.getMessage(), e);
//            result.put("success", false);
//            result.put("message", "카카오 로그인 처리 중 오류가 발생했습니다.");
//            throw e;
//        }
//        return result;
//    }

    private UserVo createNewKakaoUser(KakaoUserInfoVo info) {
        UserVo user = new UserVo();
        user.setEmail(info.getEmail());
        user.setName(info.getNickname());
        user.setRole("USER");
        user.setIsActive(true);
        user.setTenantId("default");
        return user;
    }

    private UserVo updateKakaoUserInfo(UserVo existing, KakaoUserInfoVo info) {
        // 필요 시 필드 업데이트 로직 추가
        return existing;
    }
}
