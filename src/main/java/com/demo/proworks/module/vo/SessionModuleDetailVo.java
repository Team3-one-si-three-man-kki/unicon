package com.demo.proworks.module.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "세션모듈상세정보")
public class SessionModuleDetailVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    public SessionModuleDetailVo(){
    }

    @ElDtoField(logicalName = "session_id", physicalName = "sessionId", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String sessionId;

    @ElDtoField(logicalName = "module_id", physicalName = "moduleId", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String moduleId;

    @ElDtoField(logicalName = "session_module_config", physicalName = "sessionModuleConfig", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String sessionModuleConfig;

    @ElDtoField(logicalName = "code", physicalName = "code", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String code;

    @ElDtoField(logicalName = "name", physicalName = "name", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String name;

    @ElDtoField(logicalName = "description", physicalName = "description", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String description;

    @ElDtoField(logicalName = "icon", physicalName = "icon", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String icon;

    @ElVoField(physicalName = "sessionId")
    public String getSessionId(){
        String ret = this.sessionId;
        return ret;
    }

    @ElVoField(physicalName = "sessionId")
    public void setSessionId(String sessionId){
        this.sessionId = sessionId;
    }

    @ElVoField(physicalName = "moduleId")
    public String getModuleId(){
        String ret = this.moduleId;
        return ret;
    }

    @ElVoField(physicalName = "moduleId")
    public void setModuleId(String moduleId){
        this.moduleId = moduleId;
    }

    @ElVoField(physicalName = "sessionModuleConfig")
    public String getSessionModuleConfig(){
        String ret = this.sessionModuleConfig;
        return ret;
    }

    @ElVoField(physicalName = "sessionModuleConfig")
    public void setSessionModuleConfig(String sessionModuleConfig){
        this.sessionModuleConfig = sessionModuleConfig;
    }

    @ElVoField(physicalName = "code")
    public String getCode(){
        String ret = this.code;
        return ret;
    }

    @ElVoField(physicalName = "code")
    public void setCode(String code){
        this.code = code;
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

    @ElVoField(physicalName = "description")
    public String getDescription(){
        String ret = this.description;
        return ret;
    }

    @ElVoField(physicalName = "description")
    public void setDescription(String description){
        this.description = description;
    }

    @ElVoField(physicalName = "icon")
    public String getIcon(){
        String ret = this.icon;
        return ret;
    }

    @ElVoField(physicalName = "icon")
    public void setIcon(String icon){
        this.icon = icon;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SessionModuleDetailVo [");
        sb.append("sessionId").append("=").append(sessionId).append(",");
        sb.append("moduleId").append("=").append(moduleId).append(",");
        sb.append("sessionModuleConfig").append("=").append(sessionModuleConfig).append(",");
        sb.append("code").append("=").append(code).append(",");
        sb.append("name").append("=").append(name).append(",");
        sb.append("description").append("=").append(description).append(",");
        sb.append("icon").append("=").append(icon);
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
