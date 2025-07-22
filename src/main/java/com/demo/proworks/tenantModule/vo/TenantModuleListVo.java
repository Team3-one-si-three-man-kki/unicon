package com.demo.proworks.tenantModule.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", logicalName = "테넌트모듈정보")
public class TenantModuleListVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    @ElDtoField(logicalName = "테넌트모듈정보List", physicalName = "tenantModuleVoList", type = "com.demo.proworks.tenantModule.TenantModuleVo", typeKind = "List", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private java.util.List<com.demo.proworks.tenantModule.vo.TenantModuleVo> tenantModuleVoList;

    public java.util.List<com.demo.proworks.tenantModule.vo.TenantModuleVo> getTenantModuleVoList(){
        return tenantModuleVoList;
    }

    public void setTenantModuleVoList(java.util.List<com.demo.proworks.tenantModule.vo.TenantModuleVo> tenantModuleVoList){
        this.tenantModuleVoList = tenantModuleVoList;
    }

    @Override
    public String toString() {
        return "TenantModuleListVo [tenantModuleVoList=" + tenantModuleVoList+ "]";
    }

    public boolean isFixedLengthVo() {
        return false;
    }

}
