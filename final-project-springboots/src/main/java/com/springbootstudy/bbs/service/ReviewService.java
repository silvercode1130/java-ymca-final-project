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

	// 검색 기능
    public List<ReviewVO> search(String searchType, String keyword) {
        return reviewMapper.search(searchType, keyword);
    }
	
}
