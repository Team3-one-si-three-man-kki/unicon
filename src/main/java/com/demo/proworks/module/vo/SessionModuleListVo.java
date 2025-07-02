package com.demo.proworks.module.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", logicalName = "세션모듈설정정보")
public class SessionModuleListVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    @ElDtoField(logicalName = "세션모듈설정정보List", physicalName = "sessionModuleVoList", type = "com.demo.proworks.module.SessionModuleVo", typeKind = "List", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private java.util.List<com.demo.proworks.module.vo.SessionModuleVo> sessionModuleVoList;

    public java.util.List<com.demo.proworks.module.vo.SessionModuleVo> getSessionModuleVoList(){
        return sessionModuleVoList;
    }

    public void setSessionModuleVoList(java.util.List<com.demo.proworks.module.vo.SessionModuleVo> sessionModuleVoList){
        this.sessionModuleVoList = sessionModuleVoList;
    }

    @Override
    public String toString() {
        return "SessionModuleListVo [sessionModuleVoList=" + sessionModuleVoList+ "]";
    }

    public boolean isFixedLengthVo() {
        return false;
    }

}
