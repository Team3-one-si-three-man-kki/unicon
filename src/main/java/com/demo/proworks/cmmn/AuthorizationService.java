package com.demo.proworks.cmmn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.proworks.user.dao.UserDAO;
import com.demo.proworks.user.service.UserService;
import com.demo.proworks.user.vo.UserVo;
import com.inswave.elfw.log.AppLog;
import com.inswave.elfw.util.ElBeanUtils;

@Service
public class AuthorizationService {
    
    /**
     * 사용자 권한 체크
     * @throws Exception 
     */
    public boolean hasRole(ProworksUserHeader userHeader, String requiredRole) throws Exception {
      
    	UserVo user = new UserVo();
        user = getUserByEmail(userHeader);
        String roleString = (user != null) ? user.getRole() : null;
        
        UserRole currentRole = UserRole.valueOfOrDefault(roleString);
        UserRole required = UserRole.valueOfOrDefault(requiredRole);
        System.out.println("지금 내 권한은?: "+ currentRole+", 지금 요청한 서비스가 필요한 권한은?: "+required);
        return currentRole.hasPermission(required);
        
    }
    
    /**
     * 관리자 권한 확인
     */
    public boolean isAdmin(ProworksUserHeader userHeader) {
        try {
        	 UserVo user = new UserVo();
        	 user = getUserByEmail(userHeader);
            UserRole role = UserRole.valueOf(user.getRole().toUpperCase());
            return role == UserRole.ADMIN;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 테넌트 동일성 검증
     * @throws Exception 
     */
    public boolean isSameTenant(ProworksUserHeader userHeader, String targetTenantId) throws Exception {
        UserVo user = new UserVo();
        user = getUserByEmail(userHeader);
        return user.getTenantId() != null && 
               user.getTenantId().equals(targetTenantId);
    }
    
    /**
     * 활성 사용자 확인
     * @throws Exception 
     */
    public boolean isActiveUser(ProworksUserHeader userHeader) throws Exception {
        UserVo user = new UserVo();
        user = getUserByEmail(userHeader);
        return user.isIsActive();
    }
    
      
    
    public UserVo getUserByEmail(ProworksUserHeader userHeader) throws Exception {
      
       UserService userService =
                (UserService) ElBeanUtils.getBean("userServiceImpl");
       
        try {
            System.out.println("이메일 사용자 조회 - 이메일: " + userHeader.getEmail());
            
           UserVo user = new UserVo();
         user = userService.getUserByEmail(userHeader.getEmail());
            
            if (user != null) {
                System.out.println("이메일 조회 성공 - ID: " + user.getUserId() + 
                                 ", 권한: " + user.getRole() + 
                                 ", 테넌트: " + user.getTenantId() + 
                                 ", 활성: " + user.isIsActive());
            } else {
                System.out.println("해당 이메일의 사용자를 찾을 수 없습니다.");
            }
            
            return user;
            
        } catch (Exception e) {
            AppLog.error("이메일 사용자 조회 중 오류 발생", e);
            throw e;
        }
    }
    
    
}
