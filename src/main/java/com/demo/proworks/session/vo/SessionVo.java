package com.demo.proworks.session.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "세션정보")
public class SessionVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    public SessionVo(){
    }

    @ElDtoField(logicalName = "session_id", physicalName = "sessionId", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String sessionId;

    @ElDtoField(logicalName = "name", physicalName = "name", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String name;

    @ElDtoField(logicalName = "using_dept", physicalName = "usingDept", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String usingDept;

    @ElDtoField(logicalName = "start_time", physicalName = "startTime", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String startTime;

    @ElDtoField(logicalName = "invite_link", physicalName = "inviteLink", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String inviteLink;

    @ElDtoField(logicalName = "link_expiry", physicalName = "linkExpiry", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String linkExpiry;

    @ElDtoField(logicalName = "created_by", physicalName = "createdBy", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String createdBy;

    @ElDtoField(logicalName = "tenant_id", physicalName = "tenantId", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String tenantId;

    @ElVoField(physicalName = "sessionId")
    public String getSessionId(){
        String ret = this.sessionId;
        return ret;
    }

    @ElVoField(physicalName = "sessionId")
    public void setSessionId(String sessionId){
        this.sessionId = sessionId;
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

    @ElVoField(physicalName = "usingDept")
    public String getUsingDept(){
        String ret = this.usingDept;
        return ret;
    }

    @ElVoField(physicalName = "usingDept")
    public void setUsingDept(String usingDept){
        this.usingDept = usingDept;
    }

    @ElVoField(physicalName = "startTime")
    public String getStartTime(){
        String ret = this.startTime;
        return ret;
    }

    @ElVoField(physicalName = "startTime")
    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    @ElVoField(physicalName = "inviteLink")
    public String getInviteLink(){
        String ret = this.inviteLink;
        return ret;
    }

    @ElVoField(physicalName = "inviteLink")
    public void setInviteLink(String inviteLink){
        this.inviteLink = inviteLink;
    }

    @ElVoField(physicalName = "linkExpiry")
    public String getLinkExpiry(){
        String ret = this.linkExpiry;
        return ret;
    }

    @ElVoField(physicalName = "linkExpiry")
    public void setLinkExpiry(String linkExpiry){
        this.linkExpiry = linkExpiry;
    }

    @ElVoField(physicalName = "createdBy")
    public String getCreatedBy(){
        String ret = this.createdBy;
        return ret;
    }

    @ElVoField(physicalName = "createdBy")
    public void setCreatedBy(String createdBy){
        this.createdBy = createdBy;
    }

    @ElVoField(physicalName = "tenantId")
    public String getTenantId(){
        String ret = this.tenantId;
        return ret;
    }

    @ElVoField(physicalName = "tenantId")
    public void setTenantId(String tenantId){
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SessionVo [");
        sb.append("sessionId").append("=").append(sessionId).append(",");
        sb.append("name").append("=").append(name).append(",");
        sb.append("usingDept").append("=").append(usingDept).append(",");
        sb.append("startTime").append("=").append(startTime).append(",");
        sb.append("inviteLink").append("=").append(inviteLink).append(",");
        sb.append("linkExpiry").append("=").append(linkExpiry).append(",");
        sb.append("createdBy").append("=").append(createdBy).append(",");
        sb.append("tenantId").append("=").append(tenantId);
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
