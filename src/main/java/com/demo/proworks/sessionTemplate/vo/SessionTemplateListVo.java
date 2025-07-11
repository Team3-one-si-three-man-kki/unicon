package com.demo.proworks.sessionTemplate.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", logicalName = "세션별 화면 레이아웃 정보")
public class SessionTemplateListVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    @ElDtoField(logicalName = "세션별 화면 레이아웃 정보List", physicalName = "sessionTemplateVoList", type = "com.demo.proworks.sessionTemplate.SessionTemplateVo", typeKind = "List", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private java.util.List<com.demo.proworks.sessionTemplate.vo.SessionTemplateVo> sessionTemplateVoList;

    public java.util.List<com.demo.proworks.sessionTemplate.vo.SessionTemplateVo> getSessionTemplateVoList(){
        return sessionTemplateVoList;
    }

    public void setSessionTemplateVoList(java.util.List<com.demo.proworks.sessionTemplate.vo.SessionTemplateVo> sessionTemplateVoList){
        this.sessionTemplateVoList = sessionTemplateVoList;
    }

    @Override
    public String toString() {
        return "SessionTemplateListVo [sessionTemplateVoList=" + sessionTemplateVoList+ "]";
    }

    public boolean isFixedLengthVo() {
        return false;
    }

}
