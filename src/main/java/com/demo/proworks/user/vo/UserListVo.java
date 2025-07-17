package com.demo.proworks.user.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "테넌트유저")
public class UserListVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    public UserListVo(){
    }

    @ElDtoField(logicalName = "	테넌트유저List", physicalName = "userVoList", type = "", typeKind = "List", fldYn = "No", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private java.util.List<com.demo.proworks.user.vo.UserVo> userVoList;

    @ElDtoField(logicalName = "사용자 데이터 저장", physicalName = "saveDataList", type = "", typeKind = "List", fldYn = "No", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private java.util.List<com.demo.proworks.user.vo.UserVo> saveDataList;

    @ElVoField(physicalName = "userVoList")
    public java.util.List<com.demo.proworks.user.vo.UserVo> getUserVoList(){
        return userVoList;
    }

    @ElVoField(physicalName = "userVoList")
    public void setUserVoList(java.util.List<com.demo.proworks.user.vo.UserVo> userVoList){
        this.userVoList = userVoList;
    }

    @ElVoField(physicalName = "saveDataList")
    public java.util.List<com.demo.proworks.user.vo.UserVo> getSaveDataList(){
        return saveDataList;
    }

    @ElVoField(physicalName = "saveDataList")
    public void setSaveDataList(java.util.List<com.demo.proworks.user.vo.UserVo> saveDataList){
        this.saveDataList = saveDataList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UserListVo [");
        sb.append("userVoList").append("=").append(userVoList).append(",");
        sb.append("saveDataList").append("=").append(saveDataList);
        sb.append("]");
        return sb.toString();

    }

    public boolean isFixedLengthVo() {
        return false;
    }

    @Override
    public void _xStreamEnc() {
        for( int i=0 ; userVoList != null && i < userVoList.size() ; i++ ) {
            com.demo.proworks.user.vo.UserVo vo = (com.demo.proworks.user.vo.UserVo)userVoList.get(i);
            vo._xStreamEnc();	 
        }
        for( int i=0 ; saveDataList != null && i < saveDataList.size() ; i++ ) {
            com.demo.proworks.user.vo.UserVo vo = (com.demo.proworks.user.vo.UserVo)saveDataList.get(i);
            vo._xStreamEnc();	 
        }
    }


    @Override
    public void _xStreamDec() {
        for( int i=0 ; userVoList != null && i < userVoList.size() ; i++ ) {
            com.demo.proworks.user.vo.UserVo vo = (com.demo.proworks.user.vo.UserVo)userVoList.get(i);
            vo._xStreamDec();	 
        }
        for( int i=0 ; saveDataList != null && i < saveDataList.size() ; i++ ) {
            com.demo.proworks.user.vo.UserVo vo = (com.demo.proworks.user.vo.UserVo)saveDataList.get(i);
            vo._xStreamDec();	 
        }
    }


}
