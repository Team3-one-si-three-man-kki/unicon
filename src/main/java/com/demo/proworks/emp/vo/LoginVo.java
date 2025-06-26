package com.demo.proworks.emp.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "로그인VO")
public class LoginVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    public LoginVo(){
    }

    @ElDtoField(logicalName = "아이디", physicalName = "id", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String id;

    @ElDtoField(logicalName = "비밀번호", physicalName = "pw", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String pw;

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getPw(){
        return pw;
    }

    public void setPw(String pw){
        this.pw = pw;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LoginVo [");
        sb.append("id").append("=").append(id).append(",");
        sb.append("pw").append("=").append(pw);
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
