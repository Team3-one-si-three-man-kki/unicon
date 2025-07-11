package com.demo.proworks.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.demo.proworks.user.dao.UserDAO;
import com.demo.proworks.user.service.UserService;
import com.demo.proworks.user.vo.UserVo;

/**
 * @subject : 테넌트유저 관련 처리를 담당하는 ServiceImpl
 * @description : 테넌트유저 관련 처리를 담당하는 ServiceImpl
 * @author : LEEBYUNGWOOK
 * @since : 2025/07/01
 * @modification ===========================================================
 *               DATE AUTHOR DESC
 *               ===========================================================
 *               2025/07/01 LEEBYUNGWOOK 최초 생성
 * 
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

	@Resource(name = "userDAO")
	private UserDAO userDAO;

	@Resource(name = "messageSource")
	private MessageSource messageSource;

	/**
	 * 테넌트유저 목록을 조회합니다.
	 *
	 * @process 1. 테넌트유저 페이징 처리하여 목록을 조회한다. 2. 결과 List<UserVo>을(를) 리턴한다.
	 * 
	 * @param userVo 테넌트유저 UserVo
	 * @return 테넌트유저 목록 List<UserVo>
	 * @throws Exception
	 */
	public List<UserVo> selectListUser(UserVo userVo) throws Exception {
		List<UserVo> list = userDAO.selectListUser(userVo);

		return list;
	}

	/**
	 * 조회한 테넌트유저 전체 카운트
	 *
	 * @process 1. 테넌트유저 조회하여 전체 카운트를 리턴한다.
	 * 
	 * @param userVo 테넌트유저 UserVo
	 * @return 테넌트유저 목록 전체 카운트
	 * @throws Exception
	 */
	public long selectListCountUser(UserVo userVo) throws Exception {
		return userDAO.selectListCountUser(userVo);
	}

	/**
	 * 테넌트유저를 상세 조회한다.
	 *
	 * @process 1. 테넌트유저를 상세 조회한다. 2. 결과 UserVo을(를) 리턴한다.
	 * 
	 * @param userVo 테넌트유저 UserVo
	 * @return 단건 조회 결과
	 * @throws Exception
	 */
	public UserVo selectUser(UserVo userVo) throws Exception {
		UserVo resultVO = userDAO.selectUser(userVo);

		return resultVO;
	}

	/**
	 * 테넌트유저를 등록 처리 한다.
	 *
	 * @process 1. 테넌트유저를 등록 처리 한다.
	 * 
	 * @param userVo 테넌트유저 UserVo
	 * @return 번호
	 * @throws Exception
	 */
	public int insertUser(UserVo userVo) throws Exception {
		return userDAO.insertUser(userVo);
	}

	/**
	 * 테넌트유저를 갱신 처리 한다.
	 *
	 * @process 1. 테넌트유저를 갱신 처리 한다.
	 * 
	 * @param userVo 테넌트유저 UserVo
	 * @return 번호
	 * @throws Exception
	 */
	public int updateUser(UserVo userVo) throws Exception {
		return userDAO.updateUser(userVo);
	}

	/**
	 * 테넌트유저를 삭제 처리 한다.
	 *
	 * @process 1. 테넌트유저를 삭제 처리 한다.
	 * 
	 * @param userVo 테넌트유저 UserVo
	 * @return 번호
	 * @throws Exception
	 */
	public int deleteUser(UserVo userVo) throws Exception {
		return userDAO.deleteUser(userVo);
	}

	public UserVo loginUser(UserVo userVo) throws Exception {
		System.out.println("로그인서비스까진 잘된건가? " + userVo);
		UserVo resultVO = userDAO.loginUser(userVo);
		return resultVO;
	}

	public UserVo getUserByEmail(String email) throws Exception {

		return userDAO.getUserByEmail(email);
	}

	@Override
	public List<UserVo> selectUsersByTenant(UserVo vo) throws Exception {
		return userDAO.selectUsersByTenant(vo);
	}

	@Override
	@Transactional // 전체 처리를 하나의 트랜잭션으로
	public void saveUserList(List<UserVo> userList) throws Exception {
		for (UserVo userVo : userList) {
			switch (userVo.getRowStatus()) {
			case "C":
				userDAO.insertUser(userVo);
				break;
			case "U":
				userDAO.updateUser(userVo);
				break;
			case "D":
				userDAO.deleteUser(userVo);
				break;
			}
		}
	}

	@Override
	@Transactional
	public void saveUserListBatch(List<UserVo> userList) throws Exception {
		if (userList == null || userList.isEmpty()) {
			return;
		}

		// rowStatus별로 데이터 분리
		List<UserVo> insertList = new ArrayList<>();
		List<UserVo> updateList = new ArrayList<>();
		List<UserVo> deleteList = new ArrayList<>();

		for (UserVo user : userList) {
			String rowStatus = user.getRowStatus();
			if ("C".equals(rowStatus)) {
				insertList.add(user);
			} else if ("U".equals(rowStatus)) {
				updateList.add(user);
			} else if ("D".equals(rowStatus)) {
				deleteList.add(user);
			}
		}

		// 배치 처리 실행
		int insertCount = 0, updateCount = 0, deleteCount = 0;

		if (!insertList.isEmpty()) {
			insertCount = userDAO.insertUserBatch(insertList);
			System.out.println("배치 INSERT 완료: " + insertCount + "건");
		}

		if (!updateList.isEmpty()) {
			updateCount = userDAO.updateUserBatch(updateList);
			System.out.println("배치 UPDATE 완료: " + updateCount + "건");
		}

		if (!deleteList.isEmpty()) {
			deleteCount = userDAO.deleteUserBatch(deleteList);
			System.out.println("배치 DELETE 완료: " + deleteCount + "건");
		}

		System.out.println("=== 배치 처리 총 결과 ===");
		System.out.println("INSERT: " + insertCount + "건, UPDATE: " + updateCount + "건, DELETE: " + deleteCount + "건");
	}

	@Override
	public boolean isEmailAvailableInTenant(String email, String tenantId) throws Exception {
		if (email == null || email.trim().isEmpty() || tenantId == null || tenantId.trim().isEmpty()) {
			return false;
		}

		try {
			UserVo searchVo = new UserVo();
			searchVo.setEmail(email);
			searchVo.setTenantId(tenantId);

			// 같은 테넌트에서 해당 이메일을 가진 사용자 조회
			List<UserVo> existingUsers = userDAO.selectUsersByEmailAndTenant(searchVo);

			// 사용자가 없으면 사용 가능, 있으면 중복
			return existingUsers == null || existingUsers.isEmpty();

		} catch (Exception e) {
			System.err.println("테넌트별 이메일 중복 검사 중 오류: " + e.getMessage());
			throw e;
		}
	}
}