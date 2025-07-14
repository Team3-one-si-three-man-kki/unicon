package com.demo.proworks.tenantModule.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;
import java.util.List;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "테넌트모듈상세목록")
public class TenantModuleDetailVoList extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    public TenantModuleDetailVoList(){
    }

    @ElDtoField(logicalName = "테넌트모듈상세목록", physicalName = "tenantModuleDetailVoList", type = "", typeKind = "List", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private List<TenantModuleDetailVo> tenantModuleDetailVoList;

    @ElDtoField(logicalName = "총건수", physicalName = "totalCount", type = "long", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private long totalCount;

    @ElDtoField(logicalName = "페이지크기", physicalName = "pageSize", type = "long", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private long pageSize;

    @ElDtoField(logicalName = "페이지인덱스", physicalName = "pageIndex", type = "long", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private long pageIndex;

    @ElVoField(physicalName = "tenantModuleDetailVoList")
    public List<TenantModuleDetailVo> getTenantModuleDetailVoList(){
        return tenantModuleDetailVoList;
    }

    @ElVoField(physicalName = "tenantModuleDetailVoList")
    public void setTenantModuleDetailVoList(List<TenantModuleDetailVo> tenantModuleDetailVoList){
        this.tenantModuleDetailVoList = tenantModuleDetailVoList;
    }

    @ElVoField(physicalName = "totalCount")
    public long getTotalCount(){
        return totalCount;
    }

    @ElVoField(physicalName = "totalCount")
    public void setTotalCount(long totalCount){
        this.totalCount = totalCount;
    }


    @ElVoField(physicalName = "pageIndex")
    public long getPageIndex(){
        return pageIndex;
    }

    @ElVoField(physicalName = "pageIndex")
    public void setPageIndex(long pageIndex){
        this.pageIndex = pageIndex;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TenantModuleDetailVoList [");
        sb.append("tenantModuleDetailVoList").append("=").append(tenantModuleDetailVoList).append(",");
        sb.append("totalCount").append("=").append(totalCount).append(",");
        sb.append("pageSize").append("=").append(pageSize).append(",");
        sb.append("pageIndex").append("=").append(pageIndex);
        sb.append("]");
        return sb.toString();
    }

    public boolean isFixedLengthVo() {
        return false;
    }

    @Override
    public void _xStreamEnc() {
        for( int i=0 ; tenantModuleDetailVoList != null && i < tenantModuleDetailVoList.size() ; i++ ) {
            TenantModuleDetailVo vo = (TenantModuleDetailVo)tenantModuleDetailVoList.get(i);
            vo._xStreamEnc();
        }
    }

    @Override
    public void _xStreamDec() {
        for( int i=0 ; tenantModuleDetailVoList != null && i < tenantModuleDetailVoList.size() ; i++ ) {
            TenantModuleDetailVo vo = (TenantModuleDetailVo)tenantModuleDetailVoList.get(i);
            vo._xStreamDec();
        }
    }
}
