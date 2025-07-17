package com.demo.proworks.module.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "테넌트모듈")
public class TenantModuleVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    public TenantModuleVo(){
    }

    @ElDtoField(logicalName = "moduleId", physicalName = "moduleId", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String moduleId;

    @ElDtoField(logicalName = "tenantId", physicalName = "tenantId", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String tenantId;

    @ElDtoField(logicalName = "purchasedAt", physicalName = "purchasedAt", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String purchasedAt;

    @ElVoField(physicalName = "moduleId")
    public String getModuleId(){
        String ret = this.moduleId;
        return ret;
    }

    @ElVoField(physicalName = "moduleId")
    public void setModuleId(String moduleId){
        this.moduleId = moduleId;
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

    @ElVoField(physicalName = "purchasedAt")
    public String getPurchasedAt(){
        String ret = this.purchasedAt;
        return ret;
    }

    @ElVoField(physicalName = "purchasedAt")
    public void setPurchasedAt(String purchasedAt){
        this.purchasedAt = purchasedAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TenantModuleVo [");
        sb.append("moduleId").append("=").append(moduleId).append(",");
        sb.append("tenantId").append("=").append(tenantId).append(",");
        sb.append("purchasedAt").append("=").append(purchasedAt);
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
