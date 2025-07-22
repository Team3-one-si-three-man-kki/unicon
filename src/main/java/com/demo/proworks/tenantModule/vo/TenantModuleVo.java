package com.demo.proworks.tenantModule.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", logicalName = "테넌트모듈정보")
public class TenantModuleVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    @ElDtoField(logicalName = "tenant_id", physicalName = "tenantId", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String tenantId;

    @ElDtoField(logicalName = "module_id", physicalName = "moduleId", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String moduleId;

    @ElDtoField(logicalName = "purchased_at", physicalName = "purchasedAt", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String purchasedAt;

    @ElDtoField(logicalName = "search_tenant_id", physicalName = "scTenantId", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String scTenantId;

    @ElDtoField(logicalName = "search_module_id", physicalName = "scModuleId", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String scModuleId;

    @ElDtoField(logicalName = "search_purchased_at", physicalName = "scPurchasedAt", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String scPurchasedAt;

    @ElVoField(physicalName = "tenantId")
    public String getTenantId(){
        return tenantId;
    }

    @ElVoField(physicalName = "tenantId")
    public void setTenantId(String tenantId){
        this.tenantId = tenantId;
    }

    @ElVoField(physicalName = "moduleId")
    public String getModuleId(){
        return moduleId;
    }

    @ElVoField(physicalName = "moduleId")
    public void setModuleId(String moduleId){
        this.moduleId = moduleId;
    }

    @ElVoField(physicalName = "purchasedAt")
    public String getPurchasedAt(){
        return purchasedAt;
    }

    @ElVoField(physicalName = "purchasedAt")
    public void setPurchasedAt(String purchasedAt){
        this.purchasedAt = purchasedAt;
    }

    @ElVoField(physicalName = "scTenantId")
    public String getScTenantId(){
        return scTenantId;
    }

    @ElVoField(physicalName = "scTenantId")
    public void setScTenantId(String scTenantId) {
        this.scTenantId = scTenantId;
    }

    @ElVoField(physicalName = "scModuleId")
    public String getScModuleId(){
        return scModuleId;
    }

    @ElVoField(physicalName = "scModuleId")
    public void setScModuleId(String scModuleId) {
        this.scModuleId = scModuleId;
    }

    @ElVoField(physicalName = "scPurchasedAt")
    public String getScPurchasedAt(){
        return scPurchasedAt;
    }

    @ElVoField(physicalName = "scPurchasedAt")
    public void setScPurchasedAt(String scPurchasedAt) {
        this.scPurchasedAt = scPurchasedAt;
    }

    @Override
    public String toString() {
        return "TenantModuleVo [tenantId=" + tenantId + ",moduleId=" + moduleId + ",purchasedAt=" + purchasedAt + ",scTenantId=" + scTenantId + ",scModuleId=" + scModuleId + ",scPurchasedAt=" + scPurchasedAt + "]";
    }

    public boolean isFixedLengthVo() {
        return false;
    }

}
