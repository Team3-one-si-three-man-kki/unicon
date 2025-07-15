package com.demo.proworks.attendance.service.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.demo.proworks.attendance.dao.AttendanceDAO;
import com.demo.proworks.attendance.service.AttendanceService;
import com.demo.proworks.attendance.vo.AttendanceVo;

/**
 * @subject : 출석모듈 관련 처리를 담당하는 ServiceImpl
 * @description : 출석모듈 관련 처리를 담당하는 ServiceImpl
 * @author : kimsiyeon
 * @since : 2025/07/15
 * @modification ===========================================================
 *               DATE AUTHOR DESC
 *               ===========================================================
 *               2025/07/15 kimsiyeon 최초 생성
 * 
 */
@Service("attendanceServiceImpl")
public class AttendanceServiceImpl implements AttendanceService {

	@Resource(name = "attendanceDAO")
	private AttendanceDAO attendanceDAO;

	@Resource(name = "messageSource")
	private MessageSource messageSource;

	/**
	 * 출석모듈 목록을 조회합니다.
	 *
	 * @process 1. 출석모듈 페이징 처리하여 목록을 조회한다. 2. 결과 List<AttendanceVo>을(를) 리턴한다.
	 * 
	 * @param attendanceVo 출석모듈 AttendanceVo
	 * @return 출석모듈 목록 List<AttendanceVo>
	 * @throws Exception
	 */
	public List<AttendanceVo> selectListAttendance(AttendanceVo attendanceVo) throws Exception {
		List<AttendanceVo> list = attendanceDAO.selectListAttendance(attendanceVo);

		return list;
	}

	/**
	 * 조회한 출석모듈 전체 카운트
	 *
	 * @process 1. 출석모듈 조회하여 전체 카운트를 리턴한다.
	 * 
	 * @param attendanceVo 출석모듈 AttendanceVo
	 * @return 출석모듈 목록 전체 카운트
	 * @throws Exception
	 */
	public long selectListCountAttendance(AttendanceVo attendanceVo) throws Exception {
		return attendanceDAO.selectListCountAttendance(attendanceVo);
	}

	/**
	 * 출석모듈를 상세 조회한다.
	 *
	 * @process 1. 출석모듈를 상세 조회한다. 2. 결과 AttendanceVo을(를) 리턴한다.
	 * 
	 * @param attendanceVo 출석모듈 AttendanceVo
	 * @return 단건 조회 결과
	 * @throws Exception
	 */
	public AttendanceVo selectAttendance(AttendanceVo attendanceVo) throws Exception {
		AttendanceVo resultVO = attendanceDAO.selectAttendance(attendanceVo);

		return resultVO;
	}

	/**
	 * 출석모듈를 등록 처리 한다.
	 *
	 * @process 1. 출석모듈를 등록 처리 한다.
	 * 
	 * @param attendanceVo 출석모듈 AttendanceVo
	 * @return 번호
	 * @throws Exception
	 */
	public int insertAttendance(AttendanceVo attendanceVo) throws Exception {
		return attendanceDAO.insertAttendance(attendanceVo);
	}

	/**
	 * 출석모듈를 갱신 처리 한다.
	 *
	 * @process 1. 출석모듈를 갱신 처리 한다.
	 * 
	 * @param attendanceVo 출석모듈 AttendanceVo
	 * @return 번호
	 * @throws Exception
	 */
	public int updateAttendance(AttendanceVo attendanceVo) throws Exception {
		return attendanceDAO.updateAttendance(attendanceVo);
	}

	/**
	 * 출석모듈를 삭제 처리 한다.
	 *
	 * @process 1. 출석모듈를 삭제 처리 한다.
	 * 
	 * @param attendanceVo 출석모듈 AttendanceVo
	 * @return 번호
	 * @throws Exception
	 */
	public int deleteAttendance(AttendanceVo attendanceVo) throws Exception {
		return attendanceDAO.deleteAttendance(attendanceVo);
	}

	// CSV 다운로드 구현
	@Override
	public void downloadAttendanceCSV(AttendanceVo searchVo, HttpServletResponse response) throws Exception {
		try {
			// CSV 콘텐츠 생성
			String csvContent = generateAttendanceCSV(searchVo);

			// 파일명 생성 (세션ID_출석현황_날짜.csv)
			String sessionId = searchVo.getScSessionId() != null ? searchVo.getScSessionId() : "전체";
			String dateStr = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String fileName = String.format("attendance_session_%s_%s.csv", sessionId, dateStr);

			// 한글 파일명 인코딩
			String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

			// HTTP 헤더 설정
			response.setContentType("text/csv; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + encodedFileName);

			// BOM 추가 (엑셀에서 한글 깨짐 방지)
			response.getOutputStream().write(0xEF);
			response.getOutputStream().write(0xBB);
			response.getOutputStream().write(0xBF);

			// CSV 콘텐츠 출력
			response.getWriter().write(csvContent);
			response.getWriter().flush();

		} catch (IOException e) {
			throw new Exception("CSV 파일 다운로드 중 오류 발생: " + e.getMessage(), e);
		}
	}

	@Override
	public String generateAttendanceCSV(AttendanceVo searchVo) throws Exception {
		List<AttendanceVo> attendanceList = attendanceDAO.selectAttendanceForCSV(searchVo);

		StringBuilder csvContent = new StringBuilder();

		// CSV 헤더 (한글)
		csvContent.append("번호,세션ID,참여자명,이메일,접속IP,입장시간,퇴장시간,참여시간(분),상태\n");

		// 데이터 행들
		int rowNumber = 1;
		for (AttendanceVo attendance : attendanceList) {
			csvContent.append(rowNumber++).append(",");
			csvContent.append(escapeCSV(attendance.getSessionId())).append(",");
			csvContent.append(escapeCSV(attendance.getName())).append(",");
			csvContent.append(escapeCSV(attendance.getEmail())).append(",");
			csvContent.append(escapeCSV(attendance.getIpAddress())).append(",");
			csvContent.append(escapeCSV(attendance.getJoinTime())).append(",");
			csvContent.append(escapeCSV(attendance.getLeaveTime())).append(",");
			csvContent.append(escapeCSV(attendance.getParticipationMinutes())).append(",");
			csvContent.append(escapeCSV(attendance.getStatus()));
			csvContent.append("\n");
		}

		return csvContent.toString();
	}

	@Override
	public AttendanceVo getAttendanceStats(AttendanceVo searchVo) throws Exception {
		return attendanceDAO.selectAttendanceStats(searchVo);
	}

	/**
	 * CSV 특수문자 이스케이프 처리
	 */
	private String escapeCSV(String value) {
		if (value == null) {
			return "";
		}

		// 쉼표, 따옴표, 줄바꿈이 포함된 경우 따옴표로 감싸고 내부 따옴표는 더블 따옴표로 변경
		if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
			return "\"" + value.replace("\"", "\"\"") + "\"";
		}

		return value;
	}

}
