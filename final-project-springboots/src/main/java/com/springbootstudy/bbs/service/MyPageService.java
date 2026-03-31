package com.springbootstudy.bbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.domain.AuctionVO;
import com.springbootstudy.bbs.domain.BidVO;
import com.springbootstudy.bbs.domain.BoardVO;
import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.mapper.MyPageMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MyPageService {

  @Autowired
  private MyPageMapper myPageMapper;

  // 1. 회원 정보 조회
  public MemberVO getMember(long memIdx) {
    return myPageMapper.getMember(memIdx);
  }

  // 2. 내 경매 목록 조회 (입찰 수 포함)
  // 컨트롤러의 getAuctionList와 매퍼의 getAuctionList를 연결합니다.
  public List<AuctionVO> getAuctionList(long memIdx) {
    log.info("사용자 {}번의 입찰 수 포함 경매 목록을 조회합니다.", memIdx);
    return myPageMapper.getAuctionList(memIdx);
  }

  // 3. 내 입찰 내역 조회 (내가 참여한 경매)
  // 컨트롤러의 getBidList와 매퍼의 getBidListByMemIdx를 연결합니다.
  public List<BidVO> getBidList(long memIdx) {
    log.info("사용자 {}번의 입찰 참여 내역을 조회합니다.", memIdx);
    return myPageMapper.getBidListByMemIdx(memIdx);
  }

  // 4. 내 게시글 목록 조회 (게시판 글)
  // 컨트롤러의 getBoardList와 매퍼의 getBoardListByMemIdx를 연결합니다.
  public List<BoardVO> getBoardList(long memIdx) {
    log.info("사용자 {}번의 게시글 목록을 조회합니다.", memIdx);
    return myPageMapper.getBoardListByMemIdx(memIdx);
  }
}