package com.demo.proworks.cmmn;

import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    
    /**
     * 사용자 권한 체크
     */
    public boolean hasRole(ProworksUserHeader userHeader, String requiredRole) {
      
        String roleString = (userHeader != null) ? userHeader.getRole() : null;
        
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
            UserRole role = UserRole.valueOf(userHeader.getRole().toUpperCase());
            return role == UserRole.ADMIN;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 테넌트 동일성 검증
     */
    public boolean isSameTenant(ProworksUserHeader userHeader, String targetTenantId) {
        return userHeader.getTenantId() != null && 
               userHeader.getTenantId().equals(targetTenantId);
    }
    
    /**
     * 활성 사용자 확인
     */
    public boolean isActiveUser(ProworksUserHeader userHeader) {
        return userHeader.isIsActive();
    }
}
