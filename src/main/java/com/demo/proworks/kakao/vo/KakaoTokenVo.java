package com.demo.proworks.kakao.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "")
public class KakaoTokenVo {
    private static final long serialVersionUID = 1L;

    public KakaoTokenVo(){
    }

    @ElDtoField(logicalName = "access_token", physicalName = "accessToken", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String accessToken;

    @ElDtoField(logicalName = "refresh_token", physicalName = "refreshToken", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String refreshToken;

    @ElDtoField(logicalName = "token_type", physicalName = "tokenType", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String tokenType;

    @ElDtoField(logicalName = "expires_in", physicalName = "expiresIn", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String expiresIn;

    @ElVoField(physicalName = "accessToken")
    public String getAccessToken(){
        String ret = this.accessToken;
        return ret;
    }

    @ElVoField(physicalName = "accessToken")
    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

    @ElVoField(physicalName = "refreshToken")
    public String getRefreshToken(){
        String ret = this.refreshToken;
        return ret;
    }

    @ElVoField(physicalName = "refreshToken")
    public void setRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    @ElVoField(physicalName = "tokenType")
    public String getTokenType(){
        String ret = this.tokenType;
        return ret;
    }

    @ElVoField(physicalName = "tokenType")
    public void setTokenType(String tokenType){
        this.tokenType = tokenType;
    }

    @ElVoField(physicalName = "expiresIn")
    public String getExpiresIn(){
        String ret = this.expiresIn;
        return ret;
    }

    @ElVoField(physicalName = "expiresIn")
    public void setExpiresIn(String expiresIn){
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("KakaoTokenVo [");
        sb.append("accessToken").append("=").append(accessToken).append(",");
        sb.append("refreshToken").append("=").append(refreshToken).append(",");
        sb.append("tokenType").append("=").append(tokenType).append(",");
        sb.append("expiresIn").append("=").append(expiresIn);
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
