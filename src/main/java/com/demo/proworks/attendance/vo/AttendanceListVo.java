package com.demo.proworks.attendance.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", logicalName = "출석모듈")
public class AttendanceListVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    @ElDtoField(logicalName = "출석모듈List", physicalName = "attendanceVoList", type = "com.demo.proworks.attendance.AttendanceVo", typeKind = "List", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private java.util.List<com.demo.proworks.attendance.vo.AttendanceVo> attendanceVoList;

    public java.util.List<com.demo.proworks.attendance.vo.AttendanceVo> getAttendanceVoList(){
        return attendanceVoList;
    }

    public void setAttendanceVoList(java.util.List<com.demo.proworks.attendance.vo.AttendanceVo> attendanceVoList){
        this.attendanceVoList = attendanceVoList;
    }

    @Override
    public String toString() {
        return "AttendanceListVo [attendanceVoList=" + attendanceVoList+ "]";
    }

    public boolean isFixedLengthVo() {
        return false;
    }

}
