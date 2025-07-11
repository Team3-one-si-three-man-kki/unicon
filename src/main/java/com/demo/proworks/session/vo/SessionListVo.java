package com.demo.proworks.session.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", logicalName = "세션정보")
public class SessionListVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    @ElDtoField(logicalName = "세션정보List", physicalName = "sessionVoList", type = "com.demo.proworks.session.SessionVo", typeKind = "List", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private java.util.List<com.demo.proworks.session.vo.SessionVo> sessionVoList;

    public java.util.List<com.demo.proworks.session.vo.SessionVo> getSessionVoList(){
        return sessionVoList;
    }

    public void setSessionVoList(java.util.List<com.demo.proworks.session.vo.SessionVo> sessionVoList){
        this.sessionVoList = sessionVoList;
    }

    @Override
    public String toString() {
        return "SessionListVo [sessionVoList=" + sessionVoList+ "]";
    }

    public boolean isFixedLengthVo() {
        return false;
    }

}
