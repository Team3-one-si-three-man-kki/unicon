package com.demo.proworks.attendance.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "출석모듈")
public class AttendanceVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    public AttendanceVo(){
    }

    @ElDtoField(logicalName = "record_id", physicalName = "recordId", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String recordId;

    @ElDtoField(logicalName = "session_id", physicalName = "sessionId", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String sessionId;

    @ElDtoField(logicalName = "name", physicalName = "name", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String name;

    @ElDtoField(logicalName = "email", physicalName = "email", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String email;

    @ElDtoField(logicalName = "ip_address", physicalName = "ipAddress", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String ipAddress;

    @ElDtoField(logicalName = "join_time", physicalName = "joinTime", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String joinTime;

    @ElDtoField(logicalName = "leave_time", physicalName = "leaveTime", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String leaveTime;

    @ElDtoField(logicalName = "search_session_id", physicalName = "scSessionId", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String scSessionId;

    @ElDtoField(logicalName = "search_name", physicalName = "scName", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String scName;

    @ElDtoField(logicalName = "search_email", physicalName = "scEmail", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String scEmail;

    @ElDtoField(logicalName = "search_join_time", physicalName = "scJoinTime", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String scJoinTime;

    @ElDtoField(logicalName = "participationMinutes", physicalName = "participationMinutes", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String participationMinutes;

    @ElDtoField(logicalName = "status", physicalName = "status", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String status;

    @ElDtoField(logicalName = "cud", physicalName = "cud", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String cud;

    @ElDtoField(logicalName = "rowStatus", physicalName = "rowStatus", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String rowStatus;

    @ElVoField(physicalName = "recordId")
    public String getRecordId(){
        String ret = this.recordId;
        return ret;
    }

    @ElVoField(physicalName = "recordId")
    public void setRecordId(String recordId){
        this.recordId = recordId;
    }

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

    @ElVoField(physicalName = "email")
    public String getEmail(){
        String ret = this.email;
        return ret;
    }

    @ElVoField(physicalName = "email")
    public void setEmail(String email){
        this.email = email;
    }

    @ElVoField(physicalName = "ipAddress")
    public String getIpAddress(){
        String ret = this.ipAddress;
        return ret;
    }

    @ElVoField(physicalName = "ipAddress")
    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }

    @ElVoField(physicalName = "joinTime")
    public String getJoinTime(){
        String ret = this.joinTime;
        return ret;
    }

    @ElVoField(physicalName = "joinTime")
    public void setJoinTime(String joinTime){
        this.joinTime = joinTime;
    }

    @ElVoField(physicalName = "leaveTime")
    public String getLeaveTime(){
        String ret = this.leaveTime;
        return ret;
    }

    @ElVoField(physicalName = "leaveTime")
    public void setLeaveTime(String leaveTime){
        this.leaveTime = leaveTime;
    }

    @ElVoField(physicalName = "scSessionId")
    public String getScSessionId(){
        String ret = this.scSessionId;
        return ret;
    }

    @ElVoField(physicalName = "scSessionId")
    public void setScSessionId(String scSessionId){
        this.scSessionId = scSessionId;
    }

    @ElVoField(physicalName = "scName")
    public String getScName(){
        String ret = this.scName;
        return ret;
    }

    @ElVoField(physicalName = "scName")
    public void setScName(String scName){
        this.scName = scName;
    }

    @ElVoField(physicalName = "scEmail")
    public String getScEmail(){
        String ret = this.scEmail;
        return ret;
    }

    @ElVoField(physicalName = "scEmail")
    public void setScEmail(String scEmail){
        this.scEmail = scEmail;
    }

    @ElVoField(physicalName = "scJoinTime")
    public String getScJoinTime(){
        String ret = this.scJoinTime;
        return ret;
    }

    @ElVoField(physicalName = "scJoinTime")
    public void setScJoinTime(String scJoinTime){
        this.scJoinTime = scJoinTime;
    }

    @ElVoField(physicalName = "participationMinutes")
    public String getParticipationMinutes(){
        String ret = this.participationMinutes;
        return ret;
    }

    @ElVoField(physicalName = "participationMinutes")
    public void setParticipationMinutes(String participationMinutes){
        this.participationMinutes = participationMinutes;
    }

    @ElVoField(physicalName = "status")
    public String getStatus(){
        String ret = this.status;
        return ret;
    }

    @ElVoField(physicalName = "status")
    public void setStatus(String status){
        this.status = status;
    }

    @ElVoField(physicalName = "cud")
    public String getCud(){
        String ret = this.cud;
        return ret;
    }

    @ElVoField(physicalName = "cud")
    public void setCud(String cud){
        this.cud = cud;
    }

    @ElVoField(physicalName = "rowStatus")
    public String getRowStatus(){
        String ret = this.rowStatus;
        return ret;
    }

    @ElVoField(physicalName = "rowStatus")
    public void setRowStatus(String rowStatus){
        this.rowStatus = rowStatus;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AttendanceVo [");
        sb.append("recordId").append("=").append(recordId).append(",");
        sb.append("sessionId").append("=").append(sessionId).append(",");
        sb.append("name").append("=").append(name).append(",");
        sb.append("email").append("=").append(email).append(",");
        sb.append("ipAddress").append("=").append(ipAddress).append(",");
        sb.append("joinTime").append("=").append(joinTime).append(",");
        sb.append("leaveTime").append("=").append(leaveTime).append(",");
        sb.append("scSessionId").append("=").append(scSessionId).append(",");
        sb.append("scName").append("=").append(scName).append(",");
        sb.append("scEmail").append("=").append(scEmail).append(",");
        sb.append("scJoinTime").append("=").append(scJoinTime).append(",");
        sb.append("participationMinutes").append("=").append(participationMinutes).append(",");
        sb.append("status").append("=").append(status).append(",");
        sb.append("cud").append("=").append(cud).append(",");
        sb.append("rowStatus").append("=").append(rowStatus);
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
