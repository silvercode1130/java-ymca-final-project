package com.springbootstudy.bbs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.domain.ReviewVO;
import com.springbootstudy.bbs.service.ReviewService;

import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;

@Controller
public class ReviewController {
	
	@Autowired
	ReviewService reviewService; 
	
	// 리뷰 조회 창 -----------------------------------------------------------------

	@GetMapping("/review")
	public String review(HttpSession session, Model model) {

	    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");

	    List<ReviewVO> list = reviewService.getMyReviewList(loginUser.getMemIdx());

	    model.addAttribute("reviewList", list);

	    return "views/review/review";
	}
	
	// 리뷰 작성창 -----------------------------------------------------------------
	
	
	// 검색 전에도 기본 리스트가 table에 뜨도록 추가
	@GetMapping("/review/reviewWrite")
	public String reviewWrite(HttpSession session, Model model) { 
		
		MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/members/login";
		}
		
		// 로그인한 구매자 기준으로 리뷰 가능한 거래 목록 조회
		List<ReviewVO> list = reviewService.getWritableReviewList(loginUser.getMemIdx());
		model.addAttribute("reviewList", list);
		
		return "views/review/reviewWrite";  
	}
	
	
	// 리뷰 검색창 -----------------------------------------------------------------
	
	// 리뷰 작성전 검색하기(검색기능)
	@GetMapping("/review/reviewSearch")
	public String reviewSearch(@RequestParam(value="searchType", required = false) String searchType, 
							  @RequestParam(value="keyword", required = false) String keyword, 
							  HttpSession session,
							  Model model) {
		
		MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
		
		if (keyword != null && !keyword.trim().isEmpty()) {
	        List<ReviewVO> list = reviewService.search(searchType, keyword, loginUser.getMemIdx());
	        model.addAttribute("reviewList", list);
	        model.addAttribute("keyword", keyword);
	    }
		
		return "views/review/reviewWrite";  
	}
	
	 
	// 리뷰 글쓰기 -----------------------------------------------------------------
	@PostMapping("/review/reviewWrite")
	public String reviewSubmit(
	        @RequestParam("buyer_idx") Long buyerIdx,
	        @RequestParam("bid_idx") Long bidIdx,
	        @RequestParam("auction_idx") Long auctionIdx,
	        @RequestParam("bidder_idx") Long bidderIdx,
	        @RequestParam("reviewTitle") String reviewTitle,
	        @RequestParam("reviewStar") int reviewStar,
	        @RequestParam("content") String content ) {
	    ReviewVO vo = new ReviewVO();
	    vo.setBuyerIdx(buyerIdx);
	    vo.setBidIdx(bidIdx);
	    vo.setAuctionIdx(auctionIdx);
	    vo.setBidderIdx(bidderIdx);
	    vo.setReviewTitle(reviewTitle);
	    vo.setReviewStar(reviewStar);
	    vo.setContent(content);

	    reviewService.insertReview(vo);

	    return "redirect:/review";
	}

}
