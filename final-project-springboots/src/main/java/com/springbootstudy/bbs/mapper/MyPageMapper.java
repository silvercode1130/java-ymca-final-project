package com.springbootstudy.bbs.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.springbootstudy.bbs.domain.AuctionVO;
import com.springbootstudy.bbs.domain.BidVO;
import com.springbootstudy.bbs.domain.MemberVO;

@Mapper
public interface MyPageMapper {
  public MemberVO getMember(int memIdx);

  public AuctionVO getAuction(int auctionIdx);

  public BidVO getBid(int bidIdx);
}
