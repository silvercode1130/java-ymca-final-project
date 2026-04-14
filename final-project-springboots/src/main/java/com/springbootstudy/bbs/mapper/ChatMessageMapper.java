package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.springbootstudy.bbs.domain.ChatMessageVO;

@Mapper
public interface ChatMessageMapper {

    List<ChatMessageVO> findByChatroom(Long chatroomIdx);	// 해당 채팅방의 대화 불러오기
    int insertMessage(ChatMessageVO message);						// 새 메세지

}