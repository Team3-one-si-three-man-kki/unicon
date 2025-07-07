package com.demo.proworks.tenant.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "로그인커스텀페이지정보")
public class LoginPageVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    public LoginPageVo(){
    }

    @ElDtoField(logicalName = "tenant_id", physicalName = "tenantId", type = "int", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private int tenantId;

    @ElDtoField(logicalName = "config_json", physicalName = "configJson", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String configJson;

    @ElVoField(physicalName = "tenantId")
    public int getTenantId(){
        return tenantId;
    }

    @ElVoField(physicalName = "tenantId")
    public void setTenantId(int tenantId){
        this.tenantId = tenantId;
    }

    @ElVoField(physicalName = "configJson")
    public String getConfigJson(){
        String ret = this.configJson;
        return ret;
    }

    @ElVoField(physicalName = "configJson")
    public void setConfigJson(String configJson){
        this.configJson = configJson;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LoginPageVo [");
        sb.append("tenantId").append("=").append(tenantId).append(",");
        sb.append("configJson").append("=").append(configJson);
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
