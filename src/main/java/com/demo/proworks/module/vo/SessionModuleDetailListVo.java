package com.demo.proworks.module.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "세션모듈상세정보리스트")
public class SessionModuleDetailListVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    public SessionModuleDetailListVo(){
    }

    @ElDtoField(logicalName = "sessionModuleDetailVoList", physicalName = "sessionModuleDetailVoList", type = "", typeKind = "List", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private java.util.List<com.demo.proworks.module.vo.SessionModuleDetailVo> sessionModuleDetailVoList;

    @ElVoField(physicalName = "sessionModuleDetailVoList")
    public java.util.List<com.demo.proworks.module.vo.SessionModuleDetailVo> getSessionModuleDetailVoList(){
        return sessionModuleDetailVoList;
    }

    @ElVoField(physicalName = "sessionModuleDetailVoList")
    public void setSessionModuleDetailVoList(java.util.List<com.demo.proworks.module.vo.SessionModuleDetailVo> sessionModuleDetailVoList){
        this.sessionModuleDetailVoList = sessionModuleDetailVoList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SessionModuleDetailListVo [");
        sb.append("sessionModuleDetailVoList").append("=").append(sessionModuleDetailVoList);
        sb.append("]");
        return sb.toString();

    }

    public boolean isFixedLengthVo() {
        return false;
    }

    @Override
    public void _xStreamEnc() {
        for( int i=0 ; sessionModuleDetailVoList != null && i < sessionModuleDetailVoList.size() ; i++ ) {
            com.demo.proworks.module.vo.SessionModuleDetailVo vo = (com.demo.proworks.module.vo.SessionModuleDetailVo)sessionModuleDetailVoList.get(i);
            vo._xStreamEnc();	 
        }
    }


    @Override
    public void _xStreamDec() {
        for( int i=0 ; sessionModuleDetailVoList != null && i < sessionModuleDetailVoList.size() ; i++ ) {
            com.demo.proworks.module.vo.SessionModuleDetailVo vo = (com.demo.proworks.module.vo.SessionModuleDetailVo)sessionModuleDetailVoList.get(i);
            vo._xStreamDec();	 
        }
    }


}
