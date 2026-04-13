package com.springbootstudy.bbs.service;

import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.domain.AuctionDTO;
import com.springbootstudy.bbs.domain.ChatRoomVO;
import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.mapper.ChatRoomMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomMapper chatRoomMapper;

    public Long prepareChatroomForAuction(Long auctionIdx,
                                          MemberVO loginUser,
                                          AuctionDTO detail) {

        if (loginUser == null || detail == null) {
            return null;
        }

        Long loginMemIdx = loginUser.getMemIdx();

        // ❗ 여기는 네 AuctionDTO 실제 필드명에 맞게 고쳐야 함
        Long buyerIdx = detail.getBuyerIdx(); // 예: getBuyerIdx(), getBuyerMemIdx() 등

        // 구매자는 자기 경매에서 채팅 시작 못 한다 (원하면 이 조건 지워도 됨)
        if (loginMemIdx.equals(buyerIdx)) {
            return null;
        }

        // 누구나 채팅 가능 (입찰 여부 체크 X)
        ChatRoomVO room = chatRoomMapper.findByAuctionAndMembers(
                auctionIdx,
                buyerIdx,
                loginMemIdx
        );

        if (room == null) {
            ChatRoomVO newRoom = new ChatRoomVO();
            newRoom.setAuctionIdx(auctionIdx);
            newRoom.setBuyerIdx(buyerIdx);
            newRoom.setBidderIdx(loginMemIdx);
            chatRoomMapper.insertChatRoom(newRoom);
            return newRoom.getChatroomIdx();
        }

        return room.getChatroomIdx();
    }
}