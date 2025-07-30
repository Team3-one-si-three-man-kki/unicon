package com.demo.proworks.attendance.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.demo.proworks.attendance.vo.AttendanceVo;
import com.inswave.elfw.exception.ElException;

/**
 * @subject : 출석모듈 관련 처리를 담당하는 DAO
 * @description : 출석모듈 관련 처리를 담당하는 DAO
 * @author : kimsiyeon
 * @since : 2025/07/15
 * @modification ===========================================================
 *               DATE AUTHOR DESC
 *               ===========================================================
 *               2025/07/15 kimsiyeon 최초 생성
 * 
 */
@Repository("attendanceDAO")
public class AttendanceDAO extends com.demo.proworks.cmmn.dao.ProworksDefaultAbstractDAO {

	/**
	 * 출석모듈 상세 조회한다.
	 * 
	 * @param AttendanceVo 출석모듈
	 * @return AttendanceVo 출석모듈
	 * @throws ElException
	 */
	public AttendanceVo selectAttendance(AttendanceVo vo) throws ElException {
		return (AttendanceVo) selectByPk("com.demo.proworks.attendance.selectAttendance", vo);
	}

	/**
	 * 페이징을 처리하여 출석모듈 목록조회를 한다.
	 * 
	 * @param AttendanceVo 출석모듈
	 * @return List<AttendanceVo> 출석모듈
	 * @throws ElException
	 */
	public List<AttendanceVo> selectListAttendance(AttendanceVo vo) throws ElException {
		return (List<AttendanceVo>) list("com.demo.proworks.attendance.selectListAttendance", vo);
	}

	/**
	 * 출석모듈를 등록한다.
	 * 
	 * @param AttendanceVo 출석모듈
	 * @return 번호
	 * @throws ElException
	 */
	public int insertAttendance(AttendanceVo vo) throws ElException {
		return insert("com.demo.proworks.attendance.insertAttendance", vo);
	}

	/**
	 * 출석모듈를 갱신한다.
	 * 
	 * @param AttendanceVo 출석모듈
	 * @return 번호
	 * @throws ElException
	 */
	public int updateAttendance(AttendanceVo vo) throws ElException {
		return update("com.demo.proworks.attendance.updateAttendance", vo);
	}

	/**
	 * 출석모듈를 삭제한다.
	 * 
	 * @param AttendanceVo 출석모듈
	 * @return 번호
	 * @throws ElException
	 */
	public int deleteAttendance(AttendanceVo vo) throws ElException {
		return delete("com.demo.proworks.attendance.deleteAttendance", vo);
	}

	// CSV 다운로드 전용 메소드들
	/**
	 * CSV 다운로드용 출석 목록 조회 (참여시간 계산 포함)
	 */
	public List<AttendanceVo> selectAttendanceForCSV(AttendanceVo vo) throws ElException {
		return (List<AttendanceVo>) list("com.demo.proworks.attendance.selectAttendanceForCSV", vo);
	}

	/**
	 * 세션별 출석 통계 조회
	 */
	public AttendanceVo selectAttendanceStats(AttendanceVo vo) throws ElException {
		return (AttendanceVo) selectByPk("com.demo.proworks.attendance.selectAttendanceStats", vo);
	}

	// 테넌트별 조회 메소드들 (신규 추가)
	/**
	 * 테넌트별 출석모듈 목록조회를 한다. (세션 테이블과 조인)
	 * 
	 * @param AttendanceVo 출석모듈
	 * @return List<AttendanceVo> 출석모듈
	 * @throws ElException
	 */
	public List<AttendanceVo> selectListAttendanceByTenant(AttendanceVo vo) throws ElException {
		return (List<AttendanceVo>) list("com.demo.proworks.attendance.selectListAttendanceByTenant", vo);
	}

	/**
	 * 테넌트별 출석모듈 목록 조회의 전체 카운트를 조회한다.
	 * 
	 * @param AttendanceVo 출석모듈
	 * @return 출석모듈 조회의 전체 카운트
	 * @throws ElException
	 */
	public long selectListCountAttendanceByTenant(AttendanceVo vo) throws ElException {
		return (Long) selectByPk("com.demo.proworks.attendance.selectListCountAttendanceByTenant", vo);
	}

	/**
	 * 테넌트별 CSV 다운로드용 출석 목록 조회 (참여시간 계산 포함)
	 */
	public List<AttendanceVo> selectAttendanceForCSVByTenant(AttendanceVo vo) throws ElException {
		return (List<AttendanceVo>) list("com.demo.proworks.attendance.selectAttendanceForCSVByTenant", vo);
	}

	/**
	 * 테넌트별 세션별 출석 통계 조회
	 */
	public AttendanceVo selectAttendanceStatsByTenant(AttendanceVo vo) throws ElException {
		return (AttendanceVo) selectByPk("com.demo.proworks.attendance.selectAttendanceStatsByTenant", vo);
	}

	public long selectListCountAttendance(AttendanceVo attendanceVo) throws ElException {
		return (Long) selectByPk("com.demo.proworks.attendance.selectListCountAttendance", attendanceVo);
	}
}