package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.springbootstudy.bbs.domain.BidListDTO;

@Mapper
public interface BidMapper {
	// 특정 경매글에 달린 입찰 리스트 가져오기
    List<BidListDTO> BidList(Long auctionIdx);
    
    // 입찰 등록하기
    int insertBid(BidListDTO bidDto);
}
