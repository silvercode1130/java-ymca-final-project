package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.springbootstudy.bbs.domain.AuctionListDTO;
import com.springbootstudy.bbs.domain.AuctionVO;

@Mapper
public interface AuctionMapper {
	
	// 경매 구매요청 전체 리스트 조회
	List<AuctionListDTO> auctionList();
	
	// 구매요청 상세보기 조회
	AuctionListDTO auctionDetail(Long auctionIdx);
	
	// 아이템 정보 저장 (이미지, 브랜드 등)
    int insertItem(AuctionListDTO dto);
    
    // 경매글 저장
    int insertAuction(AuctionListDTO dto);
    
    
    List<AuctionListDTO> findExpiredAuctions();
    
    
    int updateAuctionStatus(@Param("auctionIdx") Long auctionIdx, @Param("statusIdx") int statusIdx);
}
