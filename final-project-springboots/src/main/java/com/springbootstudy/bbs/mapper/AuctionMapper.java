package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.springbootstudy.bbs.domain.AuctionListDTO;
import com.springbootstudy.bbs.domain.AuctionVO;

@Mapper
public interface AuctionMapper {
	
	// 경매 구매요청 전체 리스트 조회
	List<AuctionListDTO> auctionList(@Param("keyword") String keyword, @Param("categoryIdx") Integer categoryIdx);
	
	// 구매요청 상세보기 조회
	AuctionListDTO auctionDetail(Long auctionIdx);

    // 경매글 저장
    int insertAuction(AuctionListDTO dto);
    
    
    List<AuctionListDTO> findExpiredAuctions();
    
    
    int updateAuctionStatus(@Param("auctionIdx") Long auctionIdx, @Param("statusIdx") int statusIdx);
    
    // 경매 삭제 - 소프트 딜리트 
    int softDeleteAuction(@Param("auctionIdx") Long auctionIdx, @Param("buyerIdx") Long buyerIdx);
    
    // 경매 수정
    int updateAuction(AuctionListDTO dto);
}
