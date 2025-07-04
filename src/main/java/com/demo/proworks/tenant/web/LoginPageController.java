package com.demo.proworks.tenant.web;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.proworks.tenant.service.LoginPageService;
import com.demo.proworks.tenant.service.TenantService;
import com.demo.proworks.tenant.vo.TenantVo;
import com.demo.proworks.tenant.vo.TenantListVo;

import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.annotation.ElValidator;
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
	public ResponseEntity<String> getLoginStyle(@PathVariable String subDomain) throws Exception{
		System.out.println("여기다 이녀석아!!!!");
		String configJson = loginPageService.getConfigJsonBySubDomain(subDomain);
		// String을 그대로 JSON으로 응답
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(configJson);
	}
}
