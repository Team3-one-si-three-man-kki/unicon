package com.demo.proworks.module.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", logicalName = "모듈정보")
public class ModuleListVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    @ElDtoField(logicalName = "모듈정보List", physicalName = "moduleVoList", type = "com.demo.proworks.module.ModuleVo", typeKind = "List", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private java.util.List<com.demo.proworks.module.vo.ModuleVo> moduleVoList;

    public java.util.List<com.demo.proworks.module.vo.ModuleVo> getModuleVoList(){
        return moduleVoList;
    }

    public void setModuleVoList(java.util.List<com.demo.proworks.module.vo.ModuleVo> moduleVoList){
        this.moduleVoList = moduleVoList;
    }

    @Override
    public String toString() {
        return "ModuleListVo [moduleVoList=" + moduleVoList+ "]";
    }

    public boolean isFixedLengthVo() {
        return false;
    }

}
