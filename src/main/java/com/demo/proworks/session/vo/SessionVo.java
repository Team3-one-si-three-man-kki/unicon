package com.demo.proworks.session.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", logicalName = "세션정보")
public class SessionVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    @ElDtoField(logicalName = "session_id", physicalName = "sessionId", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String sessionId;

    @ElDtoField(logicalName = "name", physicalName = "name", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String name;

    @ElDtoField(logicalName = "using_dept", physicalName = "usingDept", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String usingDept;

    @ElDtoField(logicalName = "start_time", physicalName = "startTime", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String startTime;

    @ElDtoField(logicalName = "invite_link", physicalName = "inviteLink", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String inviteLink;

    @ElDtoField(logicalName = "link_expiry", physicalName = "linkExpiry", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String linkExpiry;

    @ElDtoField(logicalName = "created_by", physicalName = "createdBy", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String createdBy;

    @ElVoField(physicalName = "sessionId")
    public String getSessionId(){
        return sessionId;
    }

    @ElVoField(physicalName = "sessionId")
    public void setSessionId(String sessionId){
        this.sessionId = sessionId;
    }

    @ElVoField(physicalName = "name")
    public String getName(){
        return name;
    }

    @ElVoField(physicalName = "name")
    public void setName(String name){
        this.name = name;
    }

    @ElVoField(physicalName = "usingDept")
    public String getUsingDept(){
        return usingDept;
    }

    @ElVoField(physicalName = "usingDept")
    public void setUsingDept(String usingDept){
        this.usingDept = usingDept;
    }

    @ElVoField(physicalName = "startTime")
    public String getStartTime(){
        return startTime;
    }

    @ElVoField(physicalName = "startTime")
    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    @ElVoField(physicalName = "inviteLink")
    public String getInviteLink(){
        return inviteLink;
    }

    @ElVoField(physicalName = "inviteLink")
    public void setInviteLink(String inviteLink){
        this.inviteLink = inviteLink;
    }

    @ElVoField(physicalName = "linkExpiry")
    public String getLinkExpiry(){
        return linkExpiry;
    }

    @ElVoField(physicalName = "linkExpiry")
    public void setLinkExpiry(String linkExpiry){
        this.linkExpiry = linkExpiry;
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
        return "SessionVo [sessionId=" + sessionId + ",name=" + name + ",usingDept=" + usingDept + ",startTime=" + startTime + ",inviteLink=" + inviteLink + ",linkExpiry=" + linkExpiry + ",createdBy=" + createdBy + "]";
    }

    public boolean isFixedLengthVo() {
        return false;
    }

}
