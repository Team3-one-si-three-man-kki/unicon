package com.demo.proworks.cmmn;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.inswave.elfw.ElConstants;
import com.inswave.elfw.core.CoreHeader;
import com.inswave.elfw.core.ElHeader;
import com.inswave.elfw.core.UserHeader;
import com.inswave.elfw.exception.ElException;
import com.inswave.elfw.exception.UserException;
import com.inswave.elfw.intercept.service.ElSystemHandleAdapter;
import com.inswave.elfw.log.AppLog;
import com.inswave.elfw.util.ControllerContextUtil;
import com.inswave.elfw.util.ElBeanUtils;
import com.inswave.elfw.view.ElMappingJacksonObjectMapper;

public class ProworksSystemHandleAdapter extends ElSystemHandleAdapter {

	public ProworksSystemHandleAdapter(Map<String, Object> adapterInfoMap) {
		super(adapterInfoMap);
	}

	@Override
	public void preHandle(HttpServletRequest request, String inputData) throws UserException {
		AppLog.debug("[ProworksSystemHandleAdapter-preHandle]...");
		String contextPath = request.getContextPath();
		String reqUri = request.getRequestURI();
		try {
			String svcId = reqUri.replaceFirst(contextPath, "");

			if (svcId.startsWith("/")) {
				svcId = svcId.substring(1);
			}

			int iSearch = svcId.lastIndexOf(".");
			String ext = "";
			if (iSearch > 0) {
				ext = svcId.substring(iSearch + 1);
				svcId = svcId.substring(0, iSearch);
			}

			new ProworksAuthProcess().checkAuth(request, svcId, inputData);

		} catch (ElException e) {
			AppLog.error("preHandle error", e);
			throw e;
		} catch (Exception e) {
			AppLog.error("preHandle error", e);
			throw new UserException("ERROR.SYS.002");
		}

	}

	@Override
	public void postHandle(ElHeader elHeader, UserHeader userHeader, Object[] serviceParams, Model resultModel) {

		boolean bSuc = elHeader.isResSuc();
		String svcId = elHeader.getServiceKey();

		long loStartTime = elHeader.getStartTime();
		long loRunTime = System.currentTimeMillis() - loStartTime;

		try {
			if (bSuc == false) {
				Object obj = elHeader.getObjArgParsingData();
				if (obj != null) {
					if (obj instanceof JsonNode) {
						JsonNode jObj = (JsonNode) obj;
						ElMappingJacksonObjectMapper elJacksonObjMapper = (ElMappingJacksonObjectMapper) ElBeanUtils
								.getBean("jsonMapper");

					}
				}
			}
		} catch (Exception e) {
			AppLog.error("postHandle Error", e);
		}

		if (resultModel == null) {
			CoreHeader coreHeader = ControllerContextUtil.getCoreHeader();

			if (coreHeader == null) {
				coreHeader = new CoreHeader();
			}

			resultModel = coreHeader.getModel();

			if (resultModel == null) {
				resultModel = new ExtendedModelMap();
			}
		}

		///////////////// ElHeader 에서 빼고 싶은 항목 설정 가능 //////////////////////
		elHeader.setObjArgParsingData(null);
		elHeader.setjSessionID(null);
		elHeader.setCookieStr(null);
		resultModel.addAttribute(ElConstants.EL_HEADER_STRING, elHeader);

		///////////////// CfwUserHeader 에서 빼고 싶은 항목 설정 가능 //////////////////////
		if (userHeader != null && userHeader instanceof ProworksUserHeader) {
			ProworksUserHeader siteUserHeader = (ProworksUserHeader) userHeader;
			siteUserHeader.setUserGroupNm(null);
			resultModel.addAttribute(ElConstants.USER_HEADER_STRING, siteUserHeader);
		}

		// ##### elHeader , userHeader, elData 만 Output 에 포함
		ArrayList<String> alDelKey = new ArrayList<String>();
		Map<String, Object> mpModel = resultModel.asMap();
		for (String key : mpModel.keySet()) {

			if (!(ElConstants.EL_HEADER_STRING.equals(key) || ElConstants.USER_HEADER_STRING.equals(key)
					|| ElConstants.EL_DATA_STRING.equals(key))) {
				alDelKey.add(key);
			}
		}
		for (String delKey : alDelKey) {
			mpModel.remove(delKey);
		}

		ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes();
		HttpServletRequest request = servletRequestAttribute.getRequest();
		request.setAttribute(ElConstants.EL_HEADER_STRING, elHeader);
		request.setAttribute(ElConstants.USER_HEADER_STRING, resultModel.getAttribute(ElConstants.USER_HEADER_STRING));

	}

}
