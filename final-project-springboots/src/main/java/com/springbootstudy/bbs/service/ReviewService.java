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
	
}
