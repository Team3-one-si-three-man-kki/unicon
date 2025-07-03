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
	
	public void checkAuth(HttpServletRequest request, String svcId, String inputData) throws Exception{
		 // 1단계: 공개 URL 체크
    if (isPublicService(svcId)) {
        AppLog.debug("공개 서비스 - 권한 체크 제외: " + svcId);
        return; // 권한 체크 없이 통과
    }
	
	
		UserHeader userHeader = ControllerContextUtil.getUserHeader();
		
//		try{
//			if( userHeader != null ) {  // 세션이 존재함 
//				if( userHeader instanceof ProworksUserHeader ) {
//					ProworksUserHeader siteUserHeader = (ProworksUserHeader)userHeader;
//					String userId = siteUserHeader.getUserId();
//					
//					// userId 기반으로   ServiceImple 을 사용하여 권한등 체크로직 수행 -> 아래는 ElBeanUtils 를 사용해서 권한 체크 하라는 예시 
//					EmpService empService = (EmpService)ElBeanUtils.getBean("empServiceImpl");  
//					boolean bCheck = false;
//					//..... 권한 체크 로직 수행 
//					
//					if( false == bCheck) { // 권한 체크 결과 
//					    throw new UserException("ERR.USER.0003");  // 권한이 존재하지 않습니다.
//					}
//				} else {
//					throw new UserException("ERR.USER.0002");  // 세션이 존재하지 않습니다.
//				}
//			} else { /// 세션이 존재하지 않으면 
//				throw new UserException("ERR.USER.0002");  // 세션이 존재하지 않습니다.
//			}
//		}catch(UserException ue){
//			AppLog.error("CommAuthProcess-UserException", ue);
//			throw ue;
//		}catch(Exception e){
//			AppLog.error("CommAuthProcess-Exception", e);
//			throw new UserException("ERR.USER.0003");  // 기타 에러 메시지....
//		}

  if (!(userHeader instanceof ProworksUserHeader)) {
            throw new UserException("ERR.USER.0002");
        }
        
        ProworksUserHeader proWorksUserHeader = (ProworksUserHeader) userHeader;
        
        // AuthorizationService를 통한 권한 체크
        AuthorizationService authService = 
            (AuthorizationService) ElBeanUtils.getBean("authorizationService");
        System.out.println("auth프로세스 헤더 뭐로넘어와?"+proWorksUserHeader);
        
        if (!authService.isActiveUser(proWorksUserHeader)) {
            throw new UserException("ERR.USER.0004"); // 비활성 계정
        }
        
        String requiredRole = getRequiredRoleForProWorksService(svcId);
        
        if (!authService.hasRole(proWorksUserHeader, requiredRole)) {
            AppLog.debug("권한 부족 - 사용자: " + proWorksUserHeader.getRole() 
                   + ", 필요: " + requiredRole);
        throw new UserException("ERR.USER.0003");
        }
        AppLog.debug("권한 체크 성공 - svcId: " + svcId + 
                ", 사용자권한: " + proWorksUserHeader.getRole());
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
    
    private boolean isPublicService(String svcId) {
    return svcId.startsWith("TNU0001Login") || 
           svcId.startsWith("TNU0002Register") ||
           svcId.startsWith("api/public");
}

}
