package com.demo.proworks.user.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "회원가입유저")
public class UserSignupVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    public UserSignupVo(){
    }

    @ElDtoField(logicalName = "사용자명", physicalName = "userName", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String userName;

    @ElDtoField(logicalName = "회사명", physicalName = "tenantName", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String tenantName;

    @ElDtoField(logicalName = "이메일", physicalName = "email", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String email;

    @ElDtoField(logicalName = "비밀번호", physicalName = "password", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String password;

    @ElDtoField(logicalName = "비밀번호확인", physicalName = "passwordConfirm", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String passwordConfirm;

    @ElDtoField(logicalName = "서브도메인", physicalName = "subDomain", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String subDomain;

    @ElVoField(physicalName = "userName")
    public String getUserName(){
        String ret = this.userName;
        return ret;
    }

    @ElVoField(physicalName = "userName")
    public void setUserName(String userName){
        this.userName = userName;
    }

    @ElVoField(physicalName = "tenantName")
    public String getTenantName(){
        String ret = this.tenantName;
        return ret;
    }

    @ElVoField(physicalName = "tenantName")
    public void setTenantName(String tenantName){
        this.tenantName = tenantName;
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

    @ElVoField(physicalName = "passwordConfirm")
    public String getPasswordConfirm(){
        String ret = this.passwordConfirm;
        return ret;
    }

    @ElVoField(physicalName = "passwordConfirm")
    public void setPasswordConfirm(String passwordConfirm){
        this.passwordConfirm = passwordConfirm;
    }

    @ElVoField(physicalName = "subDomain")
    public String getSubDomain(){
        String ret = this.subDomain;
        return ret;
    }

    @ElVoField(physicalName = "subDomain")
    public void setSubDomain(String subDomain){
        this.subDomain = subDomain;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UserSignupVo [");
        sb.append("userName").append("=").append(userName).append(",");
        sb.append("tenantName").append("=").append(tenantName).append(",");
        sb.append("email").append("=").append(email).append(",");
        sb.append("password").append("=").append(password).append(",");
        sb.append("passwordConfirm").append("=").append(passwordConfirm).append(",");
        sb.append("subDomain").append("=").append(subDomain);
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
