package com.springbootstudy.bbs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springbootstudy.bbs.domain.ChatMessageVO;
import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.service.ChatMessageService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ChatController {
	
	@Autowired
    private ChatMessageService chatMessageService;
	
	@Autowired
	private HttpSession session;
	
	// 채팅방 열기
    @GetMapping("/chats/{chatroomIdx}")
    public String chatRoom(@PathVariable("chatroomIdx") Long chatroomIdx,
                           Model model) {

        List<ChatMessageVO> messageList = chatMessageService.getMessagesByRoom(chatroomIdx);

        model.addAttribute("chatroomIdx", chatroomIdx);
        model.addAttribute("messageList", messageList);

        // templates/views/chat/chatRoom.html 반환
        return "views/chat/chatRoom";
    }
    
    // 메시지 전송하기
    @PostMapping("/chats/{chatroomIdx}/messages")
    public String sendMessage(@PathVariable("chatroomIdx") Long chatroomIdx,
                              @RequestParam("messageContent") String messageContent) {

        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) {
            // 로그인 안 되어 있으면 리다이렉트
            return "redirect:/members/login";
        }

        Long senderIdx = loginUser.getMemIdx();

        ChatMessageVO message = new ChatMessageVO();
        message.setChatroomIdx(chatroomIdx);
        message.setSenderIdx(senderIdx);
        message.setMessageContent(messageContent);

        chatMessageService.saveMessage(message);

        // 전송 후 다시 해당 채팅방으로
        return "redirect:/chats/" + chatroomIdx;
    }
}