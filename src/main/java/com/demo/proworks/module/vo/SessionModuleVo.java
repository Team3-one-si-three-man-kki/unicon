package com.demo.proworks.module.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", logicalName = "세션모듈설정정보")
public class SessionModuleVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    @ElDtoField(logicalName = "session_id", physicalName = "sessionId", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String sessionId;

    @ElDtoField(logicalName = "module_id", physicalName = "moduleId", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String moduleId;

    @ElDtoField(logicalName = "session_module_config", physicalName = "sessionModuleConfig", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String sessionModuleConfig;

    @ElVoField(physicalName = "sessionId")
    public String getSessionId(){
        return sessionId;
    }

    @ElVoField(physicalName = "sessionId")
    public void setSessionId(String sessionId){
        this.sessionId = sessionId;
    }

    @ElVoField(physicalName = "moduleId")
    public String getModuleId(){
        return moduleId;
    }

    @ElVoField(physicalName = "moduleId")
    public void setModuleId(String moduleId){
        this.moduleId = moduleId;
    }

    @ElVoField(physicalName = "sessionModuleConfig")
    public String getSessionModuleConfig(){
        return sessionModuleConfig;
    }

    @ElVoField(physicalName = "sessionModuleConfig")
    public void setSessionModuleConfig(String sessionModuleConfig){
        this.sessionModuleConfig = sessionModuleConfig;
    }

    @Override
    public String toString() {
        return "SessionModuleVo [sessionId=" + sessionId + ",moduleId=" + moduleId + ",sessionModuleConfig=" + sessionModuleConfig + "]";
    }

    public boolean isFixedLengthVo() {
        return false;
    }

}
