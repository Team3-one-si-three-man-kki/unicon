package com.demo.proworks.tenant.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", logicalName = "테넌트")
public class TenantVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    @ElDtoField(logicalName = "tenant_id", physicalName = "tenantId", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String tenantId;

    @ElDtoField(logicalName = "created_at", physicalName = "createdAt", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String createdAt;

    @ElDtoField(logicalName = "name", physicalName = "name", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String name;

    @ElDtoField(logicalName = "sub_domain", physicalName = "subDomain", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String subDomain;

    @ElDtoField(logicalName = "is_active", physicalName = "isActive", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String isActive;

    @ElDtoField(logicalName = "search_tenant_id", physicalName = "scTenantId", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String scTenantId;

    @ElDtoField(logicalName = "search_created_at", physicalName = "scCreatedAt", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String scCreatedAt;

    @ElDtoField(logicalName = "search_name", physicalName = "scName", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String scName;

    @ElDtoField(logicalName = "search_sub_domain", physicalName = "scSubDomain", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String scSubDomain;

    @ElVoField(physicalName = "tenantId")
    public String getTenantId(){
        return tenantId;
    }

    @ElVoField(physicalName = "tenantId")
    public void setTenantId(String tenantId){
        this.tenantId = tenantId;
    }

    @ElVoField(physicalName = "createdAt")
    public String getCreatedAt(){
        return createdAt;
    }

    @ElVoField(physicalName = "createdAt")
    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }

    @ElVoField(physicalName = "name")
    public String getName(){
        return name;
    }

    @ElVoField(physicalName = "name")
    public void setName(String name){
        this.name = name;
    }

    @ElVoField(physicalName = "subDomain")
    public String getSubDomain(){
        return subDomain;
    }

    @ElVoField(physicalName = "subDomain")
    public void setSubDomain(String subDomain){
        this.subDomain = subDomain;
    }

    @ElVoField(physicalName = "isActive")
    public String getIsActive(){
        return isActive;
    }

    @ElVoField(physicalName = "isActive")
    public void setIsActive(String isActive){
        this.isActive = isActive;
    }

    @ElVoField(physicalName = "scTenantId")
    public String getScTenantId(){
        return scTenantId;
    }

    @ElVoField(physicalName = "scTenantId")
    public void setScTenantId(String scTenantId) {
        this.scTenantId = scTenantId;
    }

    @ElVoField(physicalName = "scCreatedAt")
    public String getScCreatedAt(){
        return scCreatedAt;
    }

    @ElVoField(physicalName = "scCreatedAt")
    public void setScCreatedAt(String scCreatedAt) {
        this.scCreatedAt = scCreatedAt;
    }

    @ElVoField(physicalName = "scName")
    public String getScName(){
        return scName;
    }

    @ElVoField(physicalName = "scName")
    public void setScName(String scName) {
        this.scName = scName;
    }

    @ElVoField(physicalName = "scSubDomain")
    public String getScSubDomain(){
        return scSubDomain;
    }

    @ElVoField(physicalName = "scSubDomain")
    public void setScSubDomain(String scSubDomain) {
        this.scSubDomain = scSubDomain;
    }

    @Override
    public String toString() {
        return "TenantVo [tenantId=" + tenantId + ",createdAt=" + createdAt + ",name=" + name + ",subDomain=" + subDomain + ",isActive=" + isActive + ",scTenantId=" + scTenantId + ",scCreatedAt=" + scCreatedAt + ",scName=" + scName + ",scSubDomain=" + scSubDomain + "]";
    }

    public boolean isFixedLengthVo() {
        return false;
    }

}
