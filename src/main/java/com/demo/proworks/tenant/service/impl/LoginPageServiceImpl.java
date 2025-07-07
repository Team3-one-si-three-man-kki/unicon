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

	public String getConfigJsonBySubDomain(String subDomain) throws Exception {
//		LoginPageVo loginPageVo = loginPageDAO.selectLoginPageBySubDomain(subDomain);
		String loginPageVo = loginPageDAO.selectLoginPage(10);
		if (loginPageVo != null) {
			return loginPageVo;
		}
		// 설정이 없으면 기본값(빈 JSON 객체) 반환
		return "{}";
	}
	
	@Override
    public int saveLoginPageConfig(LoginPageVo vo) throws Exception {
        return loginPageDAO.saveOrUpdateLoginPage(vo);
    }

}
