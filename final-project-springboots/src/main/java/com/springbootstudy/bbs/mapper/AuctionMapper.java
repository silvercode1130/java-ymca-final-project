package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.springbootstudy.bbs.domain.AuctionDTO;
import com.springbootstudy.bbs.domain.AuctionVO;

@Mapper
public interface AuctionMapper {
	
	// 경매 구매요청 전체 리스트 조회 (검색 및 카테고리 필터)
    List<AuctionDTO> auctionList(@Param("keyword") String keyword, @Param("categoryIdx") Integer categoryIdx);

    // 구매요청 상세보기 조회
    AuctionDTO auctionDetail(Long auctionIdx);

    // 경매글 등록
    int insertAuction(AuctionDTO dto);
    
    // 마감 시간이 지난 경매 조회 (스케줄러용)
    List<AuctionDTO> findExpiredAuctions();
    
    // 경매 상태 변경 (진행중 -> 마감/유찰 등)
    int updateAuctionStatus(@Param("auctionIdx") Long auctionIdx, @Param("statusIdx") int statusIdx);
    
    // 경매 삭제 (소프트 딜리트: 실제 삭제가 아닌 상태값 변경)
    int softDeleteAuction(@Param("auctionIdx") Long auctionIdx, @Param("buyerIdx") Long buyerIdx);
    
    // 경매 수정
    int updateAuction(AuctionDTO dto);
    
}
