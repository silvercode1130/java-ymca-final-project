package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.springbootstudy.bbs.domain.BidListDTO;

@Mapper
public interface BidMapper {
	// 특정 경매글에 달린 입찰 리스트 가져오기
    List<BidListDTO> BidList(Long auctionIdx);
    
    // 아이템 등록 
    void insertItem(BidListDTO bidDto);
    
    // 입찰 등록하기
    int insertBid(BidListDTO bidDto);
    
    // 입찰 삭제 - 소프트 딜리트
    int softDeleteBid(@Param("bidIdx") Long bidIdx, @Param("bidderIdx") Long bidderIdx);
    
    // 낙찰 처리
    int selectWinnerBid(@Param("bidIdx") Long bidIdx,
                         @Param("auctionIdx") Long auctionIdx);

    // 나머지 실패 처리
    int rejectOtherBids(@Param("auctionIdx") Long auctionIdx,
                         @Param("bidIdx") Long bidIdx);

    // 입찰 상세 조회 (상세페이지용)
    BidListDTO findBidById(@Param("bidIdx") Long bidIdx);

    // 입찰 수정
    int updateBid(BidListDTO bidDto);
}
