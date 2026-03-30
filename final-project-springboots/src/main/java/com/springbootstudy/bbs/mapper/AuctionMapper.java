package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.springbootstudy.bbs.domain.AuctionListDTO;
import com.springbootstudy.bbs.domain.AuctionVO;

@Mapper
public interface AuctionMapper {
	
	// 경매 구매요청 전체 리스트 조회
	List<AuctionListDTO> auctionList();
	
	// 구매요청 상세보기 조회
	AuctionListDTO auctionDetail(Long auctionIdx);
}
