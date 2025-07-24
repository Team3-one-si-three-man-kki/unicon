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

    @ElDtoField(logicalName = "sub_domain", physicalName = "subDomain", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String subDomain;

    @ElDtoField(logicalName = "mode", physicalName = "mode", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String mode;

    @ElDtoField(logicalName = "name", physicalName = "name", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String name;

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

    @ElVoField(physicalName = "subDomain")
    public String getSubDomain(){
        String ret = this.subDomain;
        return ret;
    }

    @ElVoField(physicalName = "subDomain")
    public void setSubDomain(String subDomain){
        this.subDomain = subDomain;
    }

    @ElVoField(physicalName = "mode")
    public String getMode(){
        String ret = this.mode;
        return ret;
    }

    @ElVoField(physicalName = "mode")
    public void setMode(String mode){
        this.mode = mode;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LoginPageVo [");
        sb.append("tenantId").append("=").append(tenantId).append(",");
        sb.append("configJson").append("=").append(configJson).append(",");
        sb.append("subDomain").append("=").append(subDomain).append(",");
        sb.append("mode").append("=").append(mode).append(",");
        sb.append("name").append("=").append(name);
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
