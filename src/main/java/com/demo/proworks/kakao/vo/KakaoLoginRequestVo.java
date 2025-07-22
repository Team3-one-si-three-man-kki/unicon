package com.demo.proworks.kakao.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "")
public class KakaoLoginRequestVo {
    private static final long serialVersionUID = 1L;

    public KakaoLoginRequestVo(){
    }

    @ElDtoField(logicalName = "code", physicalName = "code", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String code;

    @ElDtoField(logicalName = "state", physicalName = "state", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String state;

    @ElVoField(physicalName = "code")
    public String getCode(){
        String ret = this.code;
        return ret;
    }

    @ElVoField(physicalName = "code")
    public void setCode(String code){
        this.code = code;
    }

    @ElVoField(physicalName = "state")
    public String getState(){
        String ret = this.state;
        return ret;
    }

    @ElVoField(physicalName = "state")
    public void setState(String state){
        this.state = state;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("KakaoLoginRequestVo [");
        sb.append("code").append("=").append(code).append(",");
        sb.append("state").append("=").append(state);
        sb.append("]");
        return sb.toString();

    }

    public boolean isFixedLengthVo() {
        return false;
    }

    public void _xStreamEnc() {
    }


    public void _xStreamDec() {
    }


}
