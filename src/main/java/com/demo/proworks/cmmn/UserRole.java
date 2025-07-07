package com.demo.proworks.cmmn;

public enum UserRole {
    ADMIN(4), MANAGER(3), USER(2), GUEST(1);
    
    private final int level;
    
    UserRole(int level) {
        this.level = level;
    }
    
    public boolean hasPermission(UserRole required) {
        return this.level >= required.level;
    }
    
    public static UserRole valueOfOrDefault(String role) {
        if (role == null || role.trim().isEmpty()) {
            return GUEST;
        }
        
        try {
            return valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return GUEST;
        }
    }
}
