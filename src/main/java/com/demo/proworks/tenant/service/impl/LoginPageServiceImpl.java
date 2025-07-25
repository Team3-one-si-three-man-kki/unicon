package com.demo.proworks.tenant.service.impl;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.demo.proworks.tenant.dao.LoginPageDAO;
import com.demo.proworks.tenant.service.LoginPageService;
import com.demo.proworks.tenant.vo.LoginPageVo;

@Service("loginPageServiceImpl")
public class LoginPageServiceImpl implements LoginPageService {

	@Resource(name = "loginPageDAO")
	private LoginPageDAO loginPageDAO;

	@Resource(name = "messageSource")
	private MessageSource messageSource;

	public LoginPageVo getConfigJsonBySubDomain(LoginPageVo subDomain) throws Exception {
		LoginPageVo config = null;
		if (subDomain.getMode().equals("login")) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>..1");
			config = loginPageDAO.selectLoginPageBySubDomain(subDomain.getSubDomain());
			System.out.println(config + "1");
		} else {
			System.out.println(">>>>>>>>>>>>>>>>>>>>..2");
			config = loginPageDAO.selectLoginPage(Integer.parseInt(subDomain.getSubDomain()));
			System.out.println(config + "2");
		}
		return config;
	}

	@Override
	public int saveLoginPageConfig(LoginPageVo vo) throws Exception {
		return loginPageDAO.saveOrUpdateLoginPage(vo);
	}

	@Override
	public int newLoginPageConfig(int tenantId) throws Exception {
		LoginPageVo vo = new LoginPageVo();
		vo.setTenantId(tenantId);
		vo.setConfigJson("{}");
		
		return loginPageDAO.newLoginPage(vo);
	}

}
