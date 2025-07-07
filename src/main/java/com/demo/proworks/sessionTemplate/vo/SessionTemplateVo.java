package com.demo.proworks.sessionTemplate.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", logicalName = "세션별 화면 레이아웃 정보")
public class SessionTemplateVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    @ElDtoField(logicalName = "template_id", physicalName = "templateId", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String templateId;

    @ElDtoField(logicalName = "session_id", physicalName = "sessionId", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String sessionId;

    @ElDtoField(logicalName = "tenant_id", physicalName = "tenantId", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String tenantId;

    @ElDtoField(logicalName = "name", physicalName = "name", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String name;

    @ElDtoField(logicalName = "created_at", physicalName = "createdAt", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String createdAt;

    @ElDtoField(logicalName = "config_json", physicalName = "configJson", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String configJson;

    @ElDtoField(logicalName = "created_by", physicalName = "createdBy", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String createdBy;

    @ElVoField(physicalName = "templateId")
    public String getTemplateId(){
        return templateId;
    }

    @ElVoField(physicalName = "templateId")
    public void setTemplateId(String templateId){
        this.templateId = templateId;
    }

    @ElVoField(physicalName = "sessionId")
    public String getSessionId(){
        return sessionId;
    }

    @ElVoField(physicalName = "sessionId")
    public void setSessionId(String sessionId){
        this.sessionId = sessionId;
    }

    @ElVoField(physicalName = "tenantId")
    public String getTenantId(){
        return tenantId;
    }

    @ElVoField(physicalName = "tenantId")
    public void setTenantId(String tenantId){
        this.tenantId = tenantId;
    }

    @ElVoField(physicalName = "name")
    public String getName(){
        return name;
    }

    @ElVoField(physicalName = "name")
    public void setName(String name){
        this.name = name;
    }

    @ElVoField(physicalName = "createdAt")
    public String getCreatedAt(){
        return createdAt;
    }

    @ElVoField(physicalName = "createdAt")
    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }

    @ElVoField(physicalName = "configJson")
    public String getConfigJson(){
        return configJson;
    }

    @ElVoField(physicalName = "configJson")
    public void setConfigJson(String configJson){
        this.configJson = configJson;
    }

    @ElVoField(physicalName = "createdBy")
    public String getCreatedBy(){
        return createdBy;
    }

    @ElVoField(physicalName = "createdBy")
    public void setCreatedBy(String createdBy){
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "SessionTemplateVo [templateId=" + templateId + ",sessionId=" + sessionId + ",tenantId=" + tenantId + ",name=" + name + ",createdAt=" + createdAt + ",configJson=" + configJson + ",createdBy=" + createdBy + "]";
    }

    public boolean isFixedLengthVo() {
        return false;
    }

}
