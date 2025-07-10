package com.demo.proworks.cmmn;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.inswave.elfw.adapter.AdapterException;
import com.inswave.elfw.exception.ElException;
import com.inswave.elfw.log.AppLog;
import com.inswave.elfw.session.SessionDataAdapter;
import com.inswave.elfw.util.ElBeanUtils;

import com.demo.proworks.emp.service.EmpService;
import com.demo.proworks.emp.vo.EmpVo;
import com.demo.proworks.jwt.JwtUtil;
import com.demo.proworks.user.service.UserService;
import com.demo.proworks.user.vo.UserVo;

/**  
 * @Class Name : ProworksSessionDataAdapter.java
 * @Description : 프로젝트 세션 데이터 어댑터 - 로그인 후 사용자 헤더 정보를 Setting 한다. 
 * @Modification Information  
 * @
 * @  수정일                  수정자                  수정내용
 * @ ---------   ---------   -------------------------------
 * @ 2019.08.01   EL개발팀             최초생성
 * 
 * @author EL개발팀
 * @since 2013.08.01
 * @version 1.0
 * @see
 * 
 *  Copyright (C) by Inswave All right reserved.
 */
public class ProworksSessionDataAdapter extends SessionDataAdapter {
	/**
	 * SessionAdapter 생성자이다.
	 * 
	 * @param adapterInfoMap Adapter 정보
	 */
	public ProworksSessionDataAdapter(Map<String, Object> adapterInfoMap) {
		super(adapterInfoMap);
	}

	/**
	 * 데모용 세션 터이터의 로드를 담당하는 구현체 메소드.
	 * - 프레임워크 SessionDataAdapter 추상클래스의 세션 데이터를 Set 하는 구현체 메소드
	 * - 프로젝트에 필요한 헤더 정보를 세팅한다. 
	 * -  해당 헤더 정보는 로그인 후에 사용가능하다. 
	 * 
	 * @param request HttpServletRequest
	 * @param id
	 * @param obj 기타 동적 파라미터에 추가할 수 있다.(ex. 서비스 구현체 )
	 * @return ProworksUserHeader
	 * @throws AdapterException
	 */
	  @Override
    public ProworksUserHeader setSessionData(HttpServletRequest request, String id, Object... obj)
            throws AdapterException {

        ProworksUserHeader userHeader = new ProworksUserHeader();
        userHeader.setUserId(id);
        userHeader.setEmail(id);

        try {
            // 1) 사용자 정보 조회
            UserService userService = (UserService) ElBeanUtils.getBean("userServiceImpl");
            String tenantId = (String) obj[1];

            UserVo userVo = new UserVo();
            userVo.setEmail(id);
            userVo.setTenantId(tenantId);

            UserVo resUserVo = userService.loginUser(userVo);
            if (resUserVo == null) {
                throw new AdapterException("EL.ERROR.LOGIN.0004", new String[]{id});
            }

            // 2) 세션 헤더에 추가 정보 설정
            //userHeader.setTenantId(resUserVo.getTenantId());
            //userHeader.setRole(resUserVo.getRole());
            //userHeader.setIsActive(resUserVo.isIsActive());

            AppLog.debug("세션 헤더 설정 완료 - " + userHeader);

        } catch (ElException e) {
            AppLog.error("setSessionData Error1", e);
            throw e;
        } catch (Exception e) {
            AppLog.error("setSessionData Error2", e);
            throw new AdapterException("EL.ERROR.LOGIN.0005");
        }

        return userHeader;
    }
}
