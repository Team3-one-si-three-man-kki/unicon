package com.demo.proworks.tenant.service.impl;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.demo.proworks.tenant.dao.LoginPageDAO;
import com.demo.proworks.tenant.service.LoginPageService;
import com.demo.proworks.tenant.vo.LoginPageVo;

@Service("LoginPageServiceImpl")
public class LoginPageServiceImpl implements LoginPageService {

	@Resource(name = "loginPageDAO")
	private LoginPageDAO loginPageDAO;

	@Resource(name = "messageSource")
	private MessageSource messageSource;

	public String getConfigJsonBySubDomain(String subDomain) throws Exception {
		LoginPageVo loginPageVo = loginPageDAO.selectLoginPageBySubDomain(subDomain);
		
		if (loginPageVo != null && loginPageVo.getConfigJson() != null) {
			return loginPageVo.getConfigJson();
		}
		// 설정이 없으면 기본값(빈 JSON 객체) 반환
		return "{}";
	}

}
