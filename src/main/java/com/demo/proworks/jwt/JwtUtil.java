package com.demo.proworks.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import com.inswave.elfw.log.AppLog;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class JwtUtil {
    
    private String jwtSecret;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;
    private String issuer;
    
    // Spring XML 설정에서 주입받기 위한 setter 메서드들
    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }
    
    public void setAccessTokenExpiration(long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }
    
    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
    
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    public String generateAccessToken(String userId, String tenantId, String role, boolean isActive) {
        Instant now = Instant.now();
        
        return Jwts.builder()
                .subject(userId)
                .claim("tenantId", tenantId)
                .claim("role", role)
                .claim("type", "access")
                .claim("isActive", isActive)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenExpiration, ChronoUnit.MILLIS)))
                .signWith(getSigningKey())
                .compact();
    }
    
    public String generateRefreshToken(String userId) {
        Instant now = Instant.now();
        
        return Jwts.builder()
                .subject(userId)
                .claim("type", "refresh")
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(refreshTokenExpiration, ChronoUnit.MILLIS)))
                .signWith(getSigningKey())
                .compact();
    }
    
    public boolean validateToken(String token) {
        if (token.isEmpty()||token=="") {
            return false;
        }
        
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            AppLog.debug("JWT 토큰 검증 실패: " + e.getMessage());
            return false;
        }
    }
    
    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }
    
    public Boolean getIsActiveFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("isActive", Boolean.class);
    }
    
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }
    
    public String getTenantIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("tenantId", String.class);
    }
    
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
