package com.demo.proworks.user.service;

import java.util.List;

import com.demo.proworks.user.vo.UserVo;

/**  
 * @subject     : 테넌트유저 관련 처리를 담당하는 인터페이스
 * @description : 테넌트유저 관련 처리를 담당하는 인터페이스
 * @author      : LEEBYUNGWOOK
 * @since       : 2025/07/01
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/01			 LEEBYUNGWOOK	 		최초 생성
 * 
 */
public interface UserService {
	
    /**
     * 테넌트유저 페이징 처리하여 목록을 조회한다.
     *
     * @param  userVo 테넌트유저 UserVo
     * @return 테넌트유저 목록 List<UserVo>
     * @throws Exception
     */
	public List<UserVo> selectListUser(UserVo userVo) throws Exception;
	
    /**
     * 조회한 테넌트유저 전체 카운트
     * 
     * @param  userVo 테넌트유저 UserVo
     * @return 테넌트유저 목록 전체 카운트
     * @throws Exception
     */
	public long selectListCountUser(UserVo userVo) throws Exception;
	
    /**
     * 테넌트유저를 상세 조회한다.
     *
     * @param  userVo 테넌트유저 UserVo
     * @return 단건 조회 결과
     * @throws Exception
     */
	public UserVo selectUser(UserVo userVo) throws Exception;
		
    /**
     * 테넌트유저를 등록 처리 한다.
     *
     * @param  userVo 테넌트유저 UserVo
     * @return 번호
     * @throws Exception
     */
	public int insertUser(UserVo userVo) throws Exception;
	
    /**
     * 테넌트유저를 갱신 처리 한다.
     *
     * @param  userVo 테넌트유저 UserVo
     * @return 번호
     * @throws Exception
     */
	public int updateUser(UserVo userVo) throws Exception;
	
    /**
     * 테넌트유저를 삭제 처리 한다.
     *
     * @param  userVo 테넌트유저 UserVo
     * @return 번호
     * @throws Exception
     */
	public int deleteUser(UserVo userVo) throws Exception;
	
	public UserVo loginUser(UserVo userVo) throws Exception;
	
}
