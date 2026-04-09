package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.springbootstudy.bbs.domain.AuctionDTO;
import com.springbootstudy.bbs.domain.AuctionVO;
import com.springbootstudy.bbs.domain.BidVO;
import com.springbootstudy.bbs.domain.BoardVO;
import com.springbootstudy.bbs.domain.MemberVO;

@Mapper
public interface MyPageMapper {

  // 1. 회원 정보 조회
  public MemberVO getMember(long memIdx);

  // 2. 내 경매 목록 조회 (입찰 수 포함)
  // 서비스의 getAuctionList와 연결됩니다.
  public List<AuctionDTO> getAuctionList(@Param("memIdx") long memIdx);

  // 3. 내 입찰 참여 내역 조회
  // 서비스의 getBidList와 연결됩니다.
  public List<BidVO> getBidListByMemIdx(long memIdx);

  // 4. 내가 쓴 게시글 목록 조회
  // 서비스의 getBoardList와 연결됩니다.
  public List<BoardVO> getBoardListByMemIdx(@Param("memIdx") Long memIdx);

  // 5. 단건 조회 기능 (필요 시 사용)
  public AuctionVO getAuction(long auctionIdx);

  public BidVO getBid(long bidIdx);
}