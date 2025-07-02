package com.demo.proworks.chat.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.demo.proworks.chat.service.ChatService;
import com.demo.proworks.chat.vo.ChatMessageVo;
import com.demo.proworks.chat.vo.JoinRoomVo;
import com.inswave.elfw.annotation.ElDescription;
import com.inswave.elfw.annotation.ElService;

/**
 * 채팅 컨트롤러
 */
@Controller
public class ChatController {

	@Autowired
	private ChatService chatService;

	/**
	 * 채팅 메시지 전송
	 */
	@ElService(key = "ChatSendMessage")
	@RequestMapping(value = "ChatSendMessage")
	@ElDescription(sub = "채팅 메시지 전송", desc = "채팅 메시지를 전송한다.")
	public ModelAndView sendMessage(ChatMessageVo chatMessageVo, HttpServletRequest request) {
	    System.out.println("=== ChatSendMessage Controller 실행됨 ==="); // 강제 로그
	
		ModelAndView mav = new ModelAndView("jsonView");
		try {
		System.out.println("받은 메시지: " + chatMessageVo.getMessage()); // 강제 로그
		
			// ProWorks 표준 서비스 호출
			chatService.sendMessage(chatMessageVo);
			mav.addObject("result", "SUCCESS");
			mav.addObject("message", "메시지 전송 성공");
		} catch (Exception e) {
			mav.addObject("result", "ERROR");
			mav.addObject("message", "메시지 전송 실패: " + e.getMessage());
		}
		return mav;
	}

	/**
	 * 채팅방 입장
	 */
	@ElService(key = "ChatJoinRoom")
	@RequestMapping(value = "ChatJoinRoom")
	@ElDescription(sub = "채팅방 입장", desc = "채팅방에 입장한다.")
	public ModelAndView joinRoom(JoinRoomVo joinRoomVo, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			String roomId = joinRoomVo.getRoomId();
			String userId = joinRoomVo.getUserId();

			System.out.println("=== ProWorks 서비스: 채팅방 입장 ===");
			System.out.println("방ID: " + roomId);
			System.out.println("사용자ID: " + userId);

			chatService.joinRoom(roomId, userId);

			mav.addObject("result", "SUCCESS");
			mav.addObject("roomId", roomId);
			mav.addObject("webSocketUrl", "ws://localhost:9093/InsWebApp/chat/" + roomId);
		} catch (Exception e) {
			mav.addObject("result", "ERROR");
			mav.addObject("message", "채팅방 입장 실패: " + e.getMessage());
		}
		return mav;
	}
}