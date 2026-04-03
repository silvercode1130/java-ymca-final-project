package com.springbootstudy.bbs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewController {
	
	// 리뷰 조회 창 -----------------------------------------------------------------

	@GetMapping("/review")
	public String review() {
		
		return "views/review/review";
	}
	
	
	// 리뷰 작성 창 -----------------------------------------------------------------
	
	@GetMapping("/review/reviewWrite")
	public String reviewWrite() {
		
		return "views/review/reviewWrite"; 
	}

}
