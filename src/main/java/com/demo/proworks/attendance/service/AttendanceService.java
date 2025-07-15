package com.demo.proworks.attendance.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.demo.proworks.attendance.vo.AttendanceVo;

/**
 * @subject : 출석모듈 관련 처리를 담당하는 인터페이스
 * @description : 출석모듈 관련 처리를 담당하는 인터페이스
 * @author : kimsiyeon
 * @since : 2025/07/15
 * @modification ===========================================================
 *               DATE AUTHOR DESC
 *               ===========================================================
 *               2025/07/15 kimsiyeon 최초 생성
 * 
 */
public interface AttendanceService {

	/**
	 * 출석모듈 페이징 처리하여 목록을 조회한다.
	 *
	 * @param attendanceVo 출석모듈 AttendanceVo
	 * @return 출석모듈 목록 List<AttendanceVo>
	 * @throws Exception
	 */
	public List<AttendanceVo> selectListAttendance(AttendanceVo attendanceVo) throws Exception;

	/**
	 * 조회한 출석모듈 전체 카운트
	 * 
	 * @param attendanceVo 출석모듈 AttendanceVo
	 * @return 출석모듈 목록 전체 카운트
	 * @throws Exception
	 */
	public long selectListCountAttendance(AttendanceVo attendanceVo) throws Exception;

	/**
	 * 출석모듈를 상세 조회한다.
	 *
	 * @param attendanceVo 출석모듈 AttendanceVo
	 * @return 단건 조회 결과
	 * @throws Exception
	 */
	public AttendanceVo selectAttendance(AttendanceVo attendanceVo) throws Exception;

	/**
	 * 출석모듈를 등록 처리 한다.
	 *
	 * @param attendanceVo 출석모듈 AttendanceVo
	 * @return 번호
	 * @throws Exception
	 */
	public int insertAttendance(AttendanceVo attendanceVo) throws Exception;

	/**
	 * 출석모듈를 갱신 처리 한다.
	 *
	 * @param attendanceVo 출석모듈 AttendanceVo
	 * @return 번호
	 * @throws Exception
	 */
	public int updateAttendance(AttendanceVo attendanceVo) throws Exception;

	/**
	 * 출석모듈를 삭제 처리 한다.
	 *
	 * @param attendanceVo 출석모듈 AttendanceVo
	 * @return 번호
	 * @throws Exception
	 */
	public int deleteAttendance(AttendanceVo attendanceVo) throws Exception;

	// CSV 다운로드 전용 메소드들
	/**
	 * 세션별 출석 현황을 CSV 파일로 다운로드
	 */
	public void downloadAttendanceCSV(AttendanceVo searchVo, HttpServletResponse response) throws Exception;

	/**
	 * CSV 콘텐츠 문자열 생성
	 */
	public String generateAttendanceCSV(AttendanceVo searchVo) throws Exception;

	/**
	 * 출석 통계 정보 조회
	 */
	public AttendanceVo getAttendanceStats(AttendanceVo searchVo) throws Exception;

}
