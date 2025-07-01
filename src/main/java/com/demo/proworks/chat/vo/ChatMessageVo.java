package com.demo.proworks.chat.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "채팅 메시지")
public class ChatMessageVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    public ChatMessageVo(){
    }

    @ElDtoField(logicalName = "message_type", physicalName = "type", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String type;

    @ElDtoField(logicalName = "message_content", physicalName = "message", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String message;

    @ElDtoField(logicalName = "sender_name", physicalName = "sender", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String sender;

    @ElDtoField(logicalName = "room_id", physicalName = "roomId", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String roomId;

    @ElDtoField(logicalName = "timestamp", physicalName = "timestamp", type = "String", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private String timestamp;

    @ElVoField(physicalName = "type")
    public String getType(){
        String ret = this.type;
        return ret;
    }

    @ElVoField(physicalName = "type")
    public void setType(String type){
        this.type = type;
    }

    @ElVoField(physicalName = "message")
    public String getMessage(){
        String ret = this.message;
        return ret;
    }

    @ElVoField(physicalName = "message")
    public void setMessage(String message){
        this.message = message;
    }

    @ElVoField(physicalName = "sender")
    public String getSender(){
        String ret = this.sender;
        return ret;
    }

    @ElVoField(physicalName = "sender")
    public void setSender(String sender){
        this.sender = sender;
    }

    @ElVoField(physicalName = "roomId")
    public String getRoomId(){
        String ret = this.roomId;
        return ret;
    }

    @ElVoField(physicalName = "roomId")
    public void setRoomId(String roomId){
        this.roomId = roomId;
    }

    @ElVoField(physicalName = "timestamp")
    public String getTimestamp(){
        String ret = this.timestamp;
        return ret;
    }

    @ElVoField(physicalName = "timestamp")
    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ChatMessageVo [");
        sb.append("type").append("=").append(type).append(",");
        sb.append("message").append("=").append(message).append(",");
        sb.append("sender").append("=").append(sender).append(",");
        sb.append("roomId").append("=").append(roomId).append(",");
        sb.append("timestamp").append("=").append(timestamp);
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
