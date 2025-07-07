package com.demo.proworks.cmmn;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.inswave.elfw.exception.ElException;
import com.inswave.elfw.log.AppLog;
import com.inswave.elfw.login.LoginAdapter;
import com.inswave.elfw.login.LoginException;
import com.inswave.elfw.login.LoginInfo;
import com.inswave.elfw.util.ElBeanUtils;

import com.demo.proworks.emp.service.EmpService;
import com.demo.proworks.emp.vo.EmpVo;
import com.demo.proworks.user.service.UserService;
import com.demo.proworks.user.vo.UserVo;
import com.demo.proworks.util.PasswordEncryptUtil;

/**
 * @subject		: ProworksLoginAdapter.java 
 * @description : 프로젝트 로그인 어댑터
 * @author		: 개발팀
 * @since 		: 2025/05/19
 * @modification
 * ===========================================================
 * DATE				AUTHOR				NOTE
 * ===========================================================
 * 2025/05/19		샘플개발팀			최초 생성
 * 
 */
public class ProworksLoginAdapter extends LoginAdapter {

    
	/**
	 * 데모용 로그인 어댑터의 생성자
	 * @param adapterInfoMap Adapter 정보
	 */
	 

    
	public ProworksLoginAdapter(Map<String, Object> adapterInfoMap){
		super(adapterInfoMap);
	}

	/**
	 * 데모용 로그인 처리를 담당하는 구현체 메소드.
	 * 프레임워크 DefaultLoginAdapter 추상클래스의 로그인 구현체 메소드
	 * @param request
	 * @param id
	 * @param params 기타 동적 파라미터에 추가할 수 있다.(ex. 서비스 구현체 )
	 * @return LoginInfo
	 * @throws LoginException
	 */
	 
	@Override
	public LoginInfo login(HttpServletRequest request, String id, Object... params) throws LoginException {


		// 로그인 체크를 수행  (샘플 예제)
//		try{
//			String pw = (String)params[0];
//			EmpService empService = (EmpService)ElBeanUtils.getBean("empServiceImpl");
//			EmpVo empVo = new EmpVo();
//
//			empVo.setEmpno(Integer.parseInt(id) );
//			EmpVo resEmpVo = empService.selectEmp(empVo);
//
//			if( resEmpVo == null ) {
//				throw new LoginException("EL.ERROR.LOGIN.0001");
//			}
//			
//			String resPw = String.valueOf(resEmpVo.getMgr());
//			if(pw == null || !pw.equals(resPw)){
//				throw new LoginException("EL.ERROR.LOGIN.0002");
//			}
//		}catch(NumberFormatException e){
//			AppLog.error("login Error1",e);
//			throw new LoginException("EL.ERROR.LOGIN.0001");
//		}catch(ElException e){
//			AppLog.error("login Error2",e);
//			throw e;		
//		}catch(Exception e){
//			AppLog.error("login Error3",e);
//			throw new LoginException("EL.ERROR.LOGIN.0003");
//		}

try{
			String pw = (String)params[0];
			String tenantId = (String)params[1];
			System.out.println(tenantId+"로그인어뎁터 테넌트아이이디~~~");
			
			
			UserService userService =
                (UserService) ElBeanUtils.getBean("userServiceImpl");
                
           PasswordEncryptUtil passwordEncryptUtil =
        		   (PasswordEncryptUtil) ElBeanUtils.getBean("passwordEncryptUtil");

			
			UserVo UserVo = new UserVo();
            UserVo.setEmail(id);
            UserVo.setTenantId(tenantId);
            System.out.println("이거로그인어뎁터에 테넌트 vo야 아이디 잘담겻나?+++++++"+UserVo);

			UserVo LoginUser = userService.loginUser(UserVo);
			System.out.println("===="+id+"====이거 아이디야, 로그인어뎁터여기에서는 불러와야해 ---------------------------------------------------------------------------.>>>>>>>>>"+LoginUser);
			if( LoginUser == null ) {
				throw new LoginException("EL.ERROR.LOGIN.0001");
			}

  System.out.println("=== 로그인 어댑터 디버깅 시작 ===");
    
    if (passwordEncryptUtil == null) {
        System.out.println("ERROR: passwordEncoder가 null입니다!");
    }
    
    if (userService == null) {
        System.out.println("ERROR: userService가 null입니다!");
    }
    
    // 111라인 직전에 추가
    System.out.println("111라인 실행 직전 - 모든 객체 상태 확인 완료");
			 String dbPw = LoginUser.getPassword();
            if (pw == null || !passwordEncryptUtil.verifyPassword(pw, dbPw)) {
                throw new LoginException("EL.ERROR.LOGIN.0002");
            }

        } catch (NumberFormatException e) {
            AppLog.error("login Error1", e);
            throw new LoginException("EL.ERROR.LOGIN.0001");
        } catch (ElException e) {
            AppLog.error("login Error2", e);
            throw e;
        } catch (Exception e) {
            AppLog.error("login Error3", e);
            throw new LoginException("EL.ERROR.LOGIN.0003");
        }


		
		// 3. 로그인 성공 설정 
		LoginInfo info = new LoginInfo();		
		info.setSuc(true);
		AppLog.debug("[Login] Proworks Login 성공.....");
		System.out.println(info.toString());
		return info;
	}

	/**
	 * 데모용 로그아웃 처리를 담당하는 구현체 메소드.
	 * 프레임워크 DefaultLoginAdapter 추상클래스의 로그아웃 구현체 메소드
	 * @param request
	 * @param id
	 * @param params 기타 동적 파라미터에 추가할 수 있다.
	 * @return LoginInfo
	 * @throws LoginException
	 */
	@Override
	public LoginInfo logout(HttpServletRequest request, String id, Object... params) throws LoginException {
		LoginInfo info = new LoginInfo();
		try{			
			// 1. 로그아웃 처리로직 추가
			
			// 2. 로그아웃 성공 설정 
			info.setSuc(true);
			AppLog.debug("[Logout] Proworks Logout 성공.....");
			
		}catch(Exception e){
			throw new LoginException(e);
		}		
		return info;
	}

}
