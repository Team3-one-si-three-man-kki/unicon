package com.demo.proworks.module.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.demo.proworks.module.service.ModuleService;
import com.demo.proworks.module.vo.ModuleVo;
import com.demo.proworks.module.dao.ModuleDAO;

/**  
 * @subject     : 모듈정보 관련 처리를 담당하는 ServiceImpl
 * @description	: 모듈정보 관련 처리를 담당하는 ServiceImpl
 * @author      : 여경원
 * @since       : 2025/07/01
 * @modification
 * ===========================================================
 * DATE				AUTHOR				DESC
 * ===========================================================
 * 2025/07/01			 여경원	 		최초 생성
 * 
 */
@Service("moduleServiceImpl")
public class ModuleServiceImpl implements ModuleService {

    @Resource(name="moduleDAO")
    private ModuleDAO moduleDAO;
	
	@Resource(name = "messageSource")
	private MessageSource messageSource;

    /**
     * 모듈정보 목록을 조회합니다.
     *
     * @process
     * 1. 모듈정보 페이징 처리하여 목록을 조회한다.
     * 2. 결과 List<ModuleVo>을(를) 리턴한다.
     * 
     * @param  moduleVo 모듈정보 ModuleVo
     * @return 모듈정보 목록 List<ModuleVo>
     * @throws Exception
     */
	public List<ModuleVo> selectListModule(ModuleVo moduleVo) throws Exception {
		List<ModuleVo> list = moduleDAO.selectListModule(moduleVo);	
	
		return list;
	}

    /**
     * 조회한 모듈정보 전체 카운트
     *
     * @process
     * 1. 모듈정보 조회하여 전체 카운트를 리턴한다.
     * 
     * @param  moduleVo 모듈정보 ModuleVo
     * @return 모듈정보 목록 전체 카운트
     * @throws Exception
     */
	public long selectListCountModule(ModuleVo moduleVo) throws Exception {
		return moduleDAO.selectListCountModule(moduleVo);
	}

    /**
     * 모듈정보를 상세 조회한다.
     *
     * @process
     * 1. 모듈정보를 상세 조회한다.
     * 2. 결과 ModuleVo을(를) 리턴한다.
     * 
     * @param  moduleVo 모듈정보 ModuleVo
     * @return 단건 조회 결과
     * @throws Exception
     */
	public ModuleVo selectModule(ModuleVo moduleVo) throws Exception {
		ModuleVo resultVO = moduleDAO.selectModule(moduleVo);			
        
        return resultVO;
	}

    /**
     * 모듈정보를 등록 처리 한다.
     *
     * @process
     * 1. 모듈정보를 등록 처리 한다.
     * 
     * @param  moduleVo 모듈정보 ModuleVo
     * @return 번호
     * @throws Exception
     */
	public int insertModule(ModuleVo moduleVo) throws Exception {
		return moduleDAO.insertModule(moduleVo);	
	}
	
    /**
     * 모듈정보를 갱신 처리 한다.
     *
     * @process
     * 1. 모듈정보를 갱신 처리 한다.
     * 
     * @param  moduleVo 모듈정보 ModuleVo
     * @return 번호
     * @throws Exception
     */
	public int updateModule(ModuleVo moduleVo) throws Exception {				
		return moduleDAO.updateModule(moduleVo);	   		
	}

    /**
     * 모듈정보를 삭제 처리 한다.
     *
     * @process
     * 1. 모듈정보를 삭제 처리 한다.
     * 
     * @param  moduleVo 모듈정보 ModuleVo
     * @return 번호
     * @throws Exception
     */
	public int deleteModule(ModuleVo moduleVo) throws Exception {
		return moduleDAO.deleteModule(moduleVo);
	}
	
}
