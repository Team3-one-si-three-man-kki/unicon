package com.demo.proworks.tenant.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", logicalName = "테넌트")
public class TenantListVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    @ElDtoField(logicalName = "테넌트List", physicalName = "tenantVoList", type = "com.demo.proworks.tenant.TenantVo", typeKind = "List", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private java.util.List<com.demo.proworks.tenant.vo.TenantVo> tenantVoList;

    public java.util.List<com.demo.proworks.tenant.vo.TenantVo> getTenantVoList(){
        return tenantVoList;
    }

    public void setTenantVoList(java.util.List<com.demo.proworks.tenant.vo.TenantVo> tenantVoList){
        this.tenantVoList = tenantVoList;
    }

    @Override
    public String toString() {
        return "TenantListVo [tenantVoList=" + tenantVoList+ "]";
    }

    public boolean isFixedLengthVo() {
        return false;
    }

}
