package com.demo.proworks.user.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "테넌트유저")
public class UserVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    public UserVo(){
    }

    @ElDtoField(logicalName = "user_id", physicalName = "userId", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String userId;

    @ElDtoField(logicalName = "tenant_id", physicalName = "tenantId", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String tenantId;

    @ElDtoField(logicalName = "name", physicalName = "name", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String name;

    @ElDtoField(logicalName = "email", physicalName = "email", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String email;

    @ElDtoField(logicalName = "password", physicalName = "password", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String password;

    @ElDtoField(logicalName = "role", physicalName = "role", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String role;

    @ElDtoField(logicalName = "created_at", physicalName = "createdAt", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String createdAt;

    @ElDtoField(logicalName = "search_tenant_id", physicalName = "scTenantId", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String scTenantId;

    @ElDtoField(logicalName = "search_name", physicalName = "scName", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String scName;

    @ElDtoField(logicalName = "search_email", physicalName = "scEmail", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String scEmail;

    @ElDtoField(logicalName = "search_role", physicalName = "scRole", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String scRole;

    @ElDtoField(logicalName = "search_created_at", physicalName = "scCreatedAt", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String scCreatedAt;

    @ElDtoField(logicalName = "is_active", physicalName = "isActive", type = "boolean", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private boolean isActive;

    @ElVoField(physicalName = "userId")
    public String getUserId(){
        String ret = this.userId;
        return ret;
    }

    @ElVoField(physicalName = "userId")
    public void setUserId(String userId){
        this.userId = userId;
    }

    @ElVoField(physicalName = "tenantId")
    public String getTenantId(){
        String ret = this.tenantId;
        return ret;
    }

    @ElVoField(physicalName = "tenantId")
    public void setTenantId(String tenantId){
        this.tenantId = tenantId;
    }

    @ElVoField(physicalName = "name")
    public String getName(){
        String ret = this.name;
        return ret;
    }

    @ElVoField(physicalName = "name")
    public void setName(String name){
        this.name = name;
    }

    @ElVoField(physicalName = "email")
    public String getEmail(){
        String ret = this.email;
        return ret;
    }

    @ElVoField(physicalName = "email")
    public void setEmail(String email){
        this.email = email;
    }

    @ElVoField(physicalName = "password")
    public String getPassword(){
        String ret = this.password;
        return ret;
    }

    @ElVoField(physicalName = "password")
    public void setPassword(String password){
        this.password = password;
    }

    @ElVoField(physicalName = "role")
    public String getRole(){
        String ret = this.role;
        return ret;
    }

    @ElVoField(physicalName = "role")
    public void setRole(String role){
        this.role = role;
    }

    @ElVoField(physicalName = "createdAt")
    public String getCreatedAt(){
        String ret = this.createdAt;
        return ret;
    }

    @ElVoField(physicalName = "createdAt")
    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }

    @ElVoField(physicalName = "scTenantId")
    public String getScTenantId(){
        String ret = this.scTenantId;
        return ret;
    }

    @ElVoField(physicalName = "scTenantId")
    public void setScTenantId(String scTenantId){
        this.scTenantId = scTenantId;
    }

    @ElVoField(physicalName = "scName")
    public String getScName(){
        String ret = this.scName;
        return ret;
    }

    @ElVoField(physicalName = "scName")
    public void setScName(String scName){
        this.scName = scName;
    }

    @ElVoField(physicalName = "scEmail")
    public String getScEmail(){
        String ret = this.scEmail;
        return ret;
    }

    @ElVoField(physicalName = "scEmail")
    public void setScEmail(String scEmail){
        this.scEmail = scEmail;
    }

    @ElVoField(physicalName = "scRole")
    public String getScRole(){
        String ret = this.scRole;
        return ret;
    }

    @ElVoField(physicalName = "scRole")
    public void setScRole(String scRole){
        this.scRole = scRole;
    }

    @ElVoField(physicalName = "scCreatedAt")
    public String getScCreatedAt(){
        String ret = this.scCreatedAt;
        return ret;
    }

    @ElVoField(physicalName = "scCreatedAt")
    public void setScCreatedAt(String scCreatedAt){
        this.scCreatedAt = scCreatedAt;
    }

    @ElVoField(physicalName = "isActive")
    public boolean isIsActive(){
        return isActive;
    }

    @ElVoField(physicalName = "isActive")
    public void setIsActive(boolean isActive){
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UserVo [");
        sb.append("userId").append("=").append(userId).append(",");
        sb.append("tenantId").append("=").append(tenantId).append(",");
        sb.append("name").append("=").append(name).append(",");
        sb.append("email").append("=").append(email).append(",");
        sb.append("password").append("=").append(password).append(",");
        sb.append("role").append("=").append(role).append(",");
        sb.append("createdAt").append("=").append(createdAt).append(",");
        sb.append("scTenantId").append("=").append(scTenantId).append(",");
        sb.append("scName").append("=").append(scName).append(",");
        sb.append("scEmail").append("=").append(scEmail).append(",");
        sb.append("scRole").append("=").append(scRole).append(",");
        sb.append("scCreatedAt").append("=").append(scCreatedAt).append(",");
        sb.append("isActive").append("=").append(isActive);
        sb.append("]");
        return sb.toString();

    }

    public boolean isFixedLengthVo() {
        return false;
    }

    @Override
    public void _xStreamEnc() {
    }


    @Override
    public void _xStreamDec() {
    }


}
