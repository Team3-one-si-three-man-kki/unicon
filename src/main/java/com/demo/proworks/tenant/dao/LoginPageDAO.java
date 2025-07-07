// LoginPageDAO.java (프로젝트 표준에 맞춘 최종 버전)

package com.demo.proworks.tenant.dao;

import org.springframework.stereotype.Repository;
// ✅ TenantDAO와 동일한, 프로젝트 공통 DAO를 상속받습니다.
import com.demo.proworks.cmmn.dao.ProworksDefaultAbstractDAO; 
import com.demo.proworks.tenant.vo.LoginPageVo;
import com.inswave.elfw.exception.ElException; // ✅ 예외 처리 클래스 추가

@Repository("loginPageDAO")
public class LoginPageDAO extends ProworksDefaultAbstractDAO {

    /**
     * Tenant ID로 로그인 페이지 설정을 조회합니다.
     */
    public String selectLoginPage(int tenantId) throws ElException {
        return (String) selectByPk("com.demo.proworks.tenant.selectLoginPage", tenantId);
    }

    /**
     * 로그인 페이지 설정을 저장하거나 수정합니다.
     */
    public int saveOrUpdateLoginPage(LoginPageVo vo) throws ElException {
        return insert("com.demo.proworks.tenant.saveOrUpdateLoginPage", vo);
    }

    /**
     * SubDomain으로 로그인 페이지 설정을 조회합니다.
     */
    public LoginPageVo selectLoginPageBySubDomain(String subDomain) throws ElException {
        return (LoginPageVo) selectByPk("com.demo.proworks.tenant.selectLoginPageBySubDomain", subDomain);
    }

}