package com.demo.proworks.cmmn;

import javax.servlet.http.HttpServletRequest;

import com.inswave.elfw.core.UserHeader;
import com.inswave.elfw.exception.UserException;
import com.inswave.elfw.log.AppLog;
import com.inswave.elfw.util.ControllerContextUtil;
import com.inswave.elfw.util.ElBeanUtils;

public class ProworksAuthProcess {


	public ProworksAuthProcess(){
	
	}
	
	public void checkAuth(HttpServletRequest request, String svcId, String inputData) throws Exception {
    String requiredRole = getRequiredRoleForProWorksService(svcId);
    if ("GUEST".equals(requiredRole)) {
        AppLog.debug("게스트서비스 서비스 - 권한 체크 제외: " + svcId + ", " + requiredRole);
        return;
    }

    // JWT 인증만 허용 (세션 기반 인증 완전 제거)
    Boolean jwtAuthenticated = (Boolean) request.getAttribute("jwtAuthenticated");
    if (jwtAuthenticated == null || !jwtAuthenticated) {
        throw new UserException("ERR.USER.0002"); // 인증 정보가 없습니다.
    }

    String userRole = (String) request.getAttribute("userRole");
    Boolean isActive = (Boolean) request.getAttribute("isActive");

    if (isActive == null || !isActive) {
        throw new UserException("ERR.USER.0004"); // 비활성 계정
    }

    UserRole current = UserRole.valueOfOrDefault(userRole);
    UserRole required = UserRole.valueOfOrDefault(requiredRole);
    if (!current.hasPermission(required)) {
        throw new UserException("ERR.USER.0003"); // 권한 부족
    }

    AppLog.debug("권한 체크 성공 - svcId: " + svcId + ", 사용자권한: " + userRole);
}
	
    
   private String getRequiredRoleForProWorksService(String svcId) {
    // 관리자 전용 서비스 (TNU0003xxx)
    if (svcId.startsWith("TNU0003")) return "ADMIN";
    
    // 매니저 전용 서비스 (TNU0002xxx)  
    if (svcId.startsWith("TNU0002")) return "MANAGER";
    
    // 사용자 서비스 (TNU0001xxx)
    if (svcId.startsWith("TNU0001")) return "USER";
    
    // 기본값: GUEST
    return "GUEST";
}


}
