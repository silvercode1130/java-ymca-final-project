package com.springbootstudy.bbs.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.domain.AuctionVO;
import com.springbootstudy.bbs.domain.BidVO;
import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.mapper.MyPageMapper;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class MyPageService {
  @Autowired
  private MyPageMapper myPageMapper;

  public MemberVO getMember(int memIdx) {
    return myPageMapper.getMember(memIdx);
  }

  public AuctionVO getAuction(int auctionIdx) {
    return myPageMapper.getAuction(auctionIdx);
  }

  public List<AuctionVO> getAuctionList(int memIdx) {
    return myPageMapper.getAuctionListByMemIdx(memIdx);
  }

  public BidVO getBid(int bidIdx) {
    return myPageMapper.getBid(bidIdx);
  }

  public List<BidVO> getBidList(int memIdx) {
    return myPageMapper.getBidListByMemIdx(memIdx);
  }
}
