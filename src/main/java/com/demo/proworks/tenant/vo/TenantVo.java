package com.demo.proworks.tenant.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "테넌트")
public class TenantVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    public TenantVo(){
    }

    @ElDtoField(logicalName = "tenant_id", physicalName = "tenantId", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String tenantId;

    @ElDtoField(logicalName = "created_at", physicalName = "createdAt", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String createdAt;

    @ElDtoField(logicalName = "name", physicalName = "name", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String name;

    @ElDtoField(logicalName = "sub_domain", physicalName = "subDomain", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String subDomain;

    @ElDtoField(logicalName = "is_active", physicalName = "isActive", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String isActive;

    @ElDtoField(logicalName = "search_tenant_id", physicalName = "scTenantId", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String scTenantId;

    @ElDtoField(logicalName = "search_created_at", physicalName = "scCreatedAt", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String scCreatedAt;

    @ElDtoField(logicalName = "search_name", physicalName = "scName", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String scName;

    @ElDtoField(logicalName = "search_sub_domain", physicalName = "scSubDomain", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String scSubDomain;

    @ElVoField(physicalName = "tenantId")
    public String getTenantId(){
        String ret = this.tenantId;
        return ret;
    }

    @ElVoField(physicalName = "tenantId")
    public void setTenantId(String tenantId){
        this.tenantId = tenantId;
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

    @ElVoField(physicalName = "name")
    public String getName(){
        String ret = this.name;
        return ret;
    }

    @ElVoField(physicalName = "name")
    public void setName(String name){
        this.name = name;
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

    @ElVoField(physicalName = "isActive")
    public String getIsActive(){
        String ret = this.isActive;
        return ret;
    }

    @ElVoField(physicalName = "isActive")
    public void setIsActive(String isActive){
        this.isActive = isActive;
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

    @ElVoField(physicalName = "scCreatedAt")
    public String getScCreatedAt(){
        String ret = this.scCreatedAt;
        return ret;
    }

    @ElVoField(physicalName = "scCreatedAt")
    public void setScCreatedAt(String scCreatedAt){
        this.scCreatedAt = scCreatedAt;
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

    @ElVoField(physicalName = "scSubDomain")
    public String getScSubDomain(){
        String ret = this.scSubDomain;
        return ret;
    }

    @ElVoField(physicalName = "scSubDomain")
    public void setScSubDomain(String scSubDomain){
        this.scSubDomain = scSubDomain;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TenantVo [");
        sb.append("tenantId").append("=").append(tenantId).append(",");
        sb.append("createdAt").append("=").append(createdAt).append(",");
        sb.append("name").append("=").append(name).append(",");
        sb.append("subDomain").append("=").append(subDomain).append(",");
        sb.append("isActive").append("=").append(isActive).append(",");
        sb.append("scTenantId").append("=").append(scTenantId).append(",");
        sb.append("scCreatedAt").append("=").append(scCreatedAt).append(",");
        sb.append("scName").append("=").append(scName).append(",");
        sb.append("scSubDomain").append("=").append(scSubDomain);
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
