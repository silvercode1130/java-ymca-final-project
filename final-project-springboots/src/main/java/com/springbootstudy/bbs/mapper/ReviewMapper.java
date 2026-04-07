package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; 

import com.springbootstudy.bbs.domain.ReviewVO;

@Mapper
public interface ReviewMapper {
	
	// 리뷰 조회
	List<ReviewVO> getMyReviewList(@Param("buyerIdx") Long buyerIdx);
	
	// 검색 전 기본 목록
	List<ReviewVO> getWritableReviewList(@Param("buyerIdx") Long buyerIdx); 
	
	// 검색 기능
	List<ReviewVO> search(@Param("searchType") String searchType,
            @Param("keyword") String keyword, @Param("buyerIdx") Long buyerIdx);
	
	// 리뷰 작성하기
	void insertReview(ReviewVO vo);
	
}