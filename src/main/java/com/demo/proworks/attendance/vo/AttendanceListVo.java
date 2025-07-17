package com.demo.proworks.attendance.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", delimeterYn = "", logicalName = "출석모듈")
public class AttendanceListVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    public AttendanceListVo(){
    }

    @ElDtoField(logicalName = "출석모듈List", physicalName = "attendanceVoList", type = "", typeKind = "List", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private java.util.List<com.demo.proworks.attendance.vo.AttendanceVo> attendanceVoList;

    @ElDtoField(logicalName = "totalCount", physicalName = "totalCount", type = "long", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private long totalCount;

    @ElDtoField(logicalName = "pageIndex ", physicalName = "pageIndex ", type = "long", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "1", desc = "", attr = "")
    private long pageIndex  = 1;

    @ElDtoField(logicalName = "totalPages", physicalName = "totalPages", type = "int", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "", desc = "", attr = "")
    private int totalPages;

    @ElDtoField(logicalName = "currentPage ", physicalName = "currentPage ", type = "int", typeKind = "", fldYn = "", delimeterYn = "", cryptoGbn = "", cryptoKind = "", length = 0, dotLen = 0, baseValue = "1", desc = "", attr = "")
    private int currentPage  = 1;

    @ElVoField(physicalName = "attendanceVoList")
    public java.util.List<com.demo.proworks.attendance.vo.AttendanceVo> getAttendanceVoList(){
        return attendanceVoList;
    }

    @ElVoField(physicalName = "attendanceVoList")
    public void setAttendanceVoList(java.util.List<com.demo.proworks.attendance.vo.AttendanceVo> attendanceVoList){
        this.attendanceVoList = attendanceVoList;
    }

    @ElVoField(physicalName = "totalCount")
    public long getTotalCount(){
        return totalCount;
    }

    @ElVoField(physicalName = "totalCount")
    public void setTotalCount(long totalCount){
        this.totalCount = totalCount;
    }

    @ElVoField(physicalName = "pageIndex ")
    public long getPageIndex (){
        return pageIndex ;
    }

    @ElVoField(physicalName = "pageIndex ")
    public void setPageIndex (long pageIndex ){
        this.pageIndex  = pageIndex ;
    }

    @ElVoField(physicalName = "totalPages")
    public int getTotalPages(){
        return totalPages;
    }

    @ElVoField(physicalName = "totalPages")
    public void setTotalPages(int totalPages){
        this.totalPages = totalPages;
    }

    @ElVoField(physicalName = "currentPage ")
    public int getCurrentPage (){
        return currentPage ;
    }

    @ElVoField(physicalName = "currentPage ")
    public void setCurrentPage (int currentPage ){
        this.currentPage  = currentPage ;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AttendanceListVo [");
        sb.append("attendanceVoList").append("=").append(attendanceVoList).append(",");
        sb.append("totalCount").append("=").append(totalCount).append(",");
        sb.append("pageIndex ").append("=").append(pageIndex ).append(",");
        sb.append("totalPages").append("=").append(totalPages).append(",");
        sb.append("currentPage ").append("=").append(currentPage );
        sb.append("]");
        return sb.toString();

    }

    public boolean isFixedLengthVo() {
        return false;
    }

    @Override
    public void _xStreamEnc() {
        for( int i=0 ; attendanceVoList != null && i < attendanceVoList.size() ; i++ ) {
            com.demo.proworks.attendance.vo.AttendanceVo vo = (com.demo.proworks.attendance.vo.AttendanceVo)attendanceVoList.get(i);
            vo._xStreamEnc();	 
        }
    }


    @Override
    public void _xStreamDec() {
        for( int i=0 ; attendanceVoList != null && i < attendanceVoList.size() ; i++ ) {
            com.demo.proworks.attendance.vo.AttendanceVo vo = (com.demo.proworks.attendance.vo.AttendanceVo)attendanceVoList.get(i);
            vo._xStreamDec();	 
        }
    }


}
