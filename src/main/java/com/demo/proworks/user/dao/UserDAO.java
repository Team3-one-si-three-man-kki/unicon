package com.demo.proworks.user.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.demo.proworks.user.vo.UserVo;
import com.inswave.elfw.exception.ElException;

/**
 * @subject : 테넌트유저 관련 처리를 담당하는 DAO
 * @description : 테넌트유저 관련 처리를 담당하는 DAO
 * @author : LEEBYUNGWOOK
 * @since : 2025/07/01
 * @modification ===========================================================
 *               DATE AUTHOR DESC
 *               ===========================================================
 *               2025/07/01 LEEBYUNGWOOK 최초 생성
 * 
 */
@Repository("userDAO")
public class UserDAO extends com.demo.proworks.cmmn.dao.ProworksDefaultAbstractDAO {

	/**
	 * 테넌트유저 상세 조회한다.
	 * 
	 * @param AttendanceVO 테넌트유저
	 * @return UserVo 테넌트유저
	 * @throws ElException
	 */
	public UserVo selectUser(UserVo vo) throws ElException {
		return (UserVo) selectByPk("com.demo.proworks.user.selectUser", vo);
	}

	/**
	 * 페이징을 처리하여 테넌트유저 목록조회를 한다.
	 * 
	 * @param AttendanceVO 테넌트유저
	 * @return List<UserVo> 테넌트유저
	 * @throws ElException
	 */
	public List<UserVo> selectListUser(UserVo vo) throws ElException {
		return (List<UserVo>) list("com.demo.proworks.user.selectListUser", vo);
	}

	/**
	 * 테넌트유저 목록 조회의 전체 카운트를 조회한다.
	 * 
	 * @param AttendanceVO 테넌트유저
	 * @return 테넌트유저 조회의 전체 카운트
	 * @throws ElException
	 */
	public long selectListCountUser(UserVo vo) throws ElException {
		return (Long) selectByPk("com.demo.proworks.user.selectListCountUser", vo);
	}

	/**
	 * 테넌트유저를 등록한다.
	 * 
	 * @param AttendanceVO 테넌트유저
	 * @return 번호
	 * @throws ElException
	 */
	public int insertUser(UserVo vo) throws ElException {
		return insert("com.demo.proworks.user.insertUser", vo);
	}

	/**
	 * 테넌트유저를 갱신한다.
	 * 
	 * @param AttendanceVO 테넌트유저
	 * @return 번호
	 * @throws ElException
	 */
	public int updateUser(UserVo vo) throws ElException {
		return update("com.demo.proworks.user.updateUser", vo);
	}

	/**
	 * 테넌트유저를 삭제한다.
	 * 
	 * @param AttendanceVO 테넌트유저
	 * @return 번호
	 * @throws ElException
	 */
	public int deleteUser(UserVo vo) throws ElException {
		return delete("com.demo.proworks.user.deleteUser", vo);
	}

	public UserVo loginUser(UserVo vo) throws ElException {
		return (UserVo) selectByPk("com.demo.proworks.user.loginUser", vo);
	}

	public UserVo getUserByEmail(String email) throws Exception {
		return (UserVo) selectByPk("com.demo.proworks.user.getUserByEmail", email);
	}

	// 사용자 목록을 조회하는 메소드 추가
	public List<UserVo> selectUsersByTenant(UserVo vo) throws ElException {
		return (List<UserVo>) list("com.demo.proworks.user.selectUsersByTenantId", vo);
	}

	/**
	 * 테넌트 ID와 이메일로 사용자를 조회한다. (이메일 중복 검사) * @param searchVo tenantId와 email을 포함한
	 * UserVo
	 * 
	 * @return List<UserVo> 조회된 사용자 목록
	 * @throws ElException
	 */
	@SuppressWarnings("unchecked")
	public List<UserVo> selectUsersByEmailAndTenant(UserVo searchVo) throws ElException {
		return (List<UserVo>) list("com.demo.proworks.user.selectUsersByEmailAndTenant", searchVo);
	}

	/**
	 * 다수의 사용자를 일괄 등록(배치)한다. * @param insertList 등록할 사용자 목록
	 * 
	 * @return int 처리 건수
	 * @throws ElException
	 */
	public int insertUserBatch(List<UserVo> insertList) throws ElException {
		return insert("com.demo.proworks.user.insertUserBatch", insertList);
	}

	/**
	 * 다수의 사용자를 일괄 갱신(배치)한다. * @param updateList 갱신할 사용자 목록
	 * 
	 * @return int 처리 건수
	 * @throws ElException
	 */
	public int updateUserBatch(List<UserVo> updateList) throws ElException {
		return update("com.demo.proworks.user.updateUserBatch", updateList);
	}

	/**
	 * 다수의 사용자를 일괄 삭제(배치)한다. * @param deleteList 삭제할 사용자 목록
	 * 
	 * @return int 처리 건수
	 * @throws ElException
	 */
	public int deleteUserBatch(List<UserVo> deleteList) throws ElException {
		return delete("com.demo.proworks.user.deleteUserBatch", deleteList);
	}

	public int deleteSessionsByUserBatch(List<UserVo> deleteList) {
		return delete("com.demo.proworks.user.deleteSessionsByUserBatch", deleteList);
	}

}
