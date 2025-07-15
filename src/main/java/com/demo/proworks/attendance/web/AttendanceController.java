package com.demo.proworks.attendance.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demo.proworks.attendance.service.AttendanceService;
import com.demo.proworks.attendance.vo.AttendanceListVo;
import com.demo.proworks.attendance.vo.AttendanceVo;
import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;
import com.inswave.elfw.annotation.ElValidator;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @subject : 출석모듈 관련 처리를 담당하는 컨트롤러
 * @description : 출석모듈 관련 처리를 담당하는 컨트롤러
 * @author : kimsiyeon
 * @since : 2025/07/15
 * @modification ===========================================================
 *               DATE AUTHOR DESC
 *               ===========================================================
 *               2025/07/15 kimsiyeon 최초 생성
 * 
 */
@Controller
public class AttendanceController {

	/** AttendanceService */
	@Resource(name = "attendanceServiceImpl")
	private AttendanceService attendanceService;

	/**
	 * 출석모듈 목록을 조회합니다.
	 *
	 * @param attendanceVo 출석모듈
	 * @return 목록조회 결과
	 * @throws Exception
	 */
	@ElService(key = "AttendanceList")
	@RequestMapping(value = "AttendanceList")
	@ElDescription(sub = "출석모듈 목록조회", desc = "페이징을 처리하여 출석모듈 목록 조회를 한다.")
	public AttendanceListVo selectListAttendance(AttendanceVo attendanceVo) throws Exception {

		List<AttendanceVo> attendanceList = attendanceService.selectListAttendance(attendanceVo);
		long totCnt = attendanceService.selectListCountAttendance(attendanceVo);

		AttendanceListVo retAttendanceList = new AttendanceListVo();
		retAttendanceList.setAttendanceVoList(attendanceList);
		retAttendanceList.setTotalCount(totCnt);
		retAttendanceList.setPageSize(attendanceVo.getPageSize());
		retAttendanceList.setPageIndex(attendanceVo.getPageIndex());

		return retAttendanceList;
	}

	/**
	 * 출석모듈을 단건 조회 처리 한다.
	 *
	 * @param attendanceVo 출석모듈
	 * @return 단건 조회 결과
	 * @throws Exception
	 */
	@ElService(key = "AttendanceUpdView")
	@RequestMapping(value = "AttendanceUpdView")
	@ElDescription(sub = "출석모듈 갱신 폼을 위한 조회", desc = "출석모듈 갱신 폼을 위한 조회를 한다.")
	public AttendanceVo selectAttendance(AttendanceVo attendanceVo) throws Exception {
		AttendanceVo selectAttendanceVo = attendanceService.selectAttendance(attendanceVo);

		return selectAttendanceVo;
	}

	/**
	 * 출석모듈를 등록 처리 한다.
	 *
	 * @param attendanceVo 출석모듈
	 * @throws Exception
	 */
	@ElService(key = "AttendanceIns")
	@RequestMapping(value = "AttendanceIns")
	@ElDescription(sub = "출석모듈 등록처리", desc = "출석모듈를 등록 처리 한다.")
	public void insertAttendance(AttendanceVo attendanceVo) throws Exception {
		attendanceService.insertAttendance(attendanceVo);
	}

	/**
	 * 출석모듈를 갱신 처리 한다.
	 *
	 * @param attendanceVo 출석모듈
	 * @throws Exception
	 */
	@ElService(key = "AttendanceUpd")
	@RequestMapping(value = "AttendanceUpd")
	@ElValidator(errUrl = "/attendance/attendanceRegister", errContinue = true)
	@ElDescription(sub = "출석모듈 갱신처리", desc = "출석모듈를 갱신 처리 한다.")
	public void updateAttendance(AttendanceVo attendanceVo) throws Exception {

		attendanceService.updateAttendance(attendanceVo);
	}

	/**
	 * 출석모듈를 삭제 처리한다.
	 *
	 * @param attendanceVo 출석모듈
	 * @throws Exception
	 */
	@ElService(key = "AttendanceDel")
	@RequestMapping(value = "AttendanceDel")
	@ElDescription(sub = "출석모듈 삭제처리", desc = "출석모듈를 삭제 처리한다.")
	public void deleteAttendance(AttendanceVo attendanceVo) throws Exception {
		attendanceService.deleteAttendance(attendanceVo);
	}

	/**
	 * 출석 현황 CSV 다운로드
	 */
	@ElService(key = "SVC_ATTENDANCE_CSV_DOWNLOAD")
	@RequestMapping(value = "SVC_ATTENDANCE_CSV_DOWNLOAD")
	@ElDescription(sub = "출석 현황 CSV 다운로드", desc = "세션별 출석 현황을 CSV 파일로 다운로드합니다.")
	public void attendanceCSVDownload(AttendanceVo attendanceVo, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			attendanceService.downloadAttendanceCSV(attendanceVo, response);
		} catch (Exception e) {
			// 오류 발생 시 에러 메시지 반환
			response.setContentType("text/plain; charset=UTF-8");
			response.getWriter().write("CSV 다운로드 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

	/**
	 * 출석 통계 조회
	 */
	@ElService(key = "SVC_ATTENDANCE_STATS")
	@RequestMapping(value = "SVC_ATTENDANCE_STATS")
	@ElDescription(sub = "출석 통계 조회", desc = "세션별 출석 통계 정보를 조회합니다.")
	@ResponseBody
	public AttendanceVo attendanceStats(AttendanceVo attendanceVo, HttpServletRequest request) throws Exception {
		return attendanceService.getAttendanceStats(attendanceVo);
	}

}
