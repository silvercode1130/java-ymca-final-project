package com.springbootstudy.bbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.domain.ReviewVO;
import com.springbootstudy.bbs.mapper.ReviewMapper;

@Service
public class ReviewService {

	
	@Autowired
    private ReviewMapper reviewMapper;
	
	// 내가 쓴 리뷰 조회
	public List<ReviewVO> getMyReviewList(Long buyerIdx) {
	    return reviewMapper.getMyReviewList(buyerIdx); 
	}
	
	// 내가 받은 리뷰 조회
	public List<ReviewVO> getReceivedReviews(Long bidIdx) {
		return reviewMapper.getReceivedReviews(bidIdx);  
	}
	
	// 검색 전 기본 목록
	public List<ReviewVO> getWritableReviewList(Long buyerIdx) {
		return reviewMapper.getWritableReviewList(buyerIdx);
	}

	// 검색 기능
	public List<ReviewVO> search(String searchType, String keyword, Long buyerIdx) {
	    return reviewMapper.search(searchType, keyword, buyerIdx);
	}

	// 리뷰 작성하기
	public void insertReview(ReviewVO vo) {
	    reviewMapper.insertReview(vo);
	}
	
	// 리뷰 상세보기
	public ReviewVO getReviewDetail(Long reviewIdx) {
	    return reviewMapper.getReviewDetail(reviewIdx);
	}
	
	// 리뷰 삭제하기(관리자만)
	public List<ReviewVO> getAllReviewList() { 
	    return reviewMapper.getAllReviewList();
	}
	
}
