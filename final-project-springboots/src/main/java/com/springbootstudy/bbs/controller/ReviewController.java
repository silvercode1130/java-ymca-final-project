package com.springbootstudy.bbs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springbootstudy.bbs.domain.ReviewVO;
import com.springbootstudy.bbs.service.ReviewService;

import org.springframework.ui.Model;

@Controller
public class ReviewController {
	
	@Autowired
	ReviewService reviewService; 
	
	// 리뷰 조회 창 -----------------------------------------------------------------

	@GetMapping("/review")
	public String review() {

		return "views/review/review";
	}
	
	
	// 리뷰 작성 창 -----------------------------------------------------------------
	
	// 리뷰 작성전 검색
	@GetMapping("/review/reviewWrite")
	public String reviewWrite(@RequestParam(value="searchType", required = false) String searchType, 
							  @RequestParam(value="keyword", required = false) String keyword, 
							  Model model) {
		
		if (keyword != null && !keyword.trim().isEmpty()) {
	        List<ReviewVO> list = reviewService.search(searchType, keyword);
	        model.addAttribute("reviewList", list);
	        model.addAttribute("keyword", keyword);
	    }
		
		
		
		return "views/review/reviewWrite";  
	}

}
