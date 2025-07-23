package com.demo.proworks.tenant.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.proworks.cmmn.ProworksUserHeader;
import com.demo.proworks.tenant.service.LoginPageService;
import com.demo.proworks.tenant.service.TenantService;
import com.demo.proworks.tenant.vo.TenantVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.demo.proworks.tenant.vo.LoginPageVo;
import com.demo.proworks.tenant.vo.TenantListVo;

import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.annotation.ElValidator;
import com.inswave.elfw.util.ControllerContextUtil;

import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @subject : 로그인 커스텀 페이지 관련 처리를 담당하는 컨트롤러
 * @description : 로그인 커스텀 페이지 관련 처리를 담당하는 컨트롤러
 * @author : LEEJAEWON
 * @since : 2025/07/04
 * @modification ===========================================================
 *               DATE AUTHOR DESC
 *               ===========================================================
 *               2025/07/01 LEEJAEWON 최초 생성
 * 
 */
@Controller
public class LoginPageController {

	/** TenantService */
	@Resource(name = "loginPageServiceImpl")
	private LoginPageService loginPageService;

	/**
	 * 테넌트 목록을 조회합니다.
	 *
	 * @param subDomain 테넌트 서브도메인 정보
	 * @return 로그인 커스텀 페이지 설정 조회를 한다.
	 * @throws Exception
	 */
	@ElService(key = "LCP0001List")
	@RequestMapping(value = "LCP0001List")
	@ElDescription(sub = "로그인 커스텀 페이지 설정 조회", desc = "로그인 커스텀 페이지 설정 조회를 한다.")
//	public ResponseEntity<String> getLoginStyle(@PathVariable String subDomain) throws Exception{
	public ResponseEntity<String> getLoginStyle(LoginPageVo tenant) throws Exception {

		String subDomain = tenant.getSubDomain();
		System.out.println(subDomain);
		LoginPageVo configVo = loginPageService.getConfigJsonBySubDomain(subDomain);

		if (configVo == null || configVo.getConfigJson() == null) {
			Map<String, String> message = new HashMap<>();
			message.put("code", "E001");
			message.put("msg", "해당 도메인을 찾을 수 없습니다.");
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
					.body(new ObjectMapper().writeValueAsString(message));
		}
		String configJson = configVo.getConfigJson();
		// String을 그대로 JSON으로 응답
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(configJson);
	}

	/**
	 * 로그인 커스텀 설정 정보를 저장한다.
	 *
	 * @param loginPageVo 로그인 커스텀 설정 정보
	 * @return 로그인 커스텀 페이지 설정 정보를 저장하
	 * @throws Exception
	 */
	@ElService(key = "LCP0001Save") // 로그인 커스텀 페이지 저장 서비스 ID
	@RequestMapping(value = "LCP0001Save")
	@ElDescription(sub = "로그인 페이지 설정 저장", desc = "로그인 페이지 커스텀 설정을 저장한다.")
	public void saveLoginPage(LoginPageVo vo) throws Exception {
		System.out.println(vo);
		System.out.println("hihi");
		// 1. ProWorks 세션에서 현재 로그인한 사용자의 헤더 정보를 가져옵니다.
//        ProworksUserHeader userHeader = (ProworksUserHeader) ControllerContextUtil.getUserHeader();

		// 2. 헤더에서 tenantId를 꺼냅니다.
//        String tenantId = userHeader.getTenantId();

		// 3. 클라이언트로부터 받은 Vo에 서버에서 직접 조회한 tenantId를 설정합니다.
		// 이렇게 하면 클라이언트가 tenantId를 보내지 않아도 됩니다.
//        vo.setTenantId(tenantId);
		vo.setTenantId(10);

		// 4. 안전하게 tenantId가 설정된 Vo를 서비스로 전달합니다.
		loginPageService.saveLoginPageConfig(vo);
	}
}
