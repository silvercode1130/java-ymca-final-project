package com.springbootstudy.bbs.service;

import com.springbootstudy.bbs.domain.ChatMessageVO;
import com.springbootstudy.bbs.mapper.ChatMessageMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
	
	@Autowired
    private ChatMessageMapper chatMessageMapper;

    public List<ChatMessageVO> getMessagesByRoom(Long chatroomIdx) {
        return chatMessageMapper.findByChatroom(chatroomIdx);
    }

    public void saveMessage(ChatMessageVO message) {
        chatMessageMapper.insertMessage(message);
    }
}