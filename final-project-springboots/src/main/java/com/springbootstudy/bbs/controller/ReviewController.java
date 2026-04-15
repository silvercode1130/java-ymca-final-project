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

	@GetMapping("/mypage/reviews") 
	public String review(HttpSession session, Model model) {

	    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
	    
	    // 로그인 안하면 로그인 부터
	    if (loginUser == null) {
	        return "redirect:/members/login";
	    }

	    // 내가 쓴 리뷰
	    List<ReviewVO> list = reviewService.getMyReviewList(loginUser.getMemIdx());
	    // 내가 받은 리뷰
	    List<ReviewVO> receivedReviewList = reviewService.getReceivedReviews(loginUser.getMemIdx());
	    // 내가 받은 리뷰 별점 평균 
	    Double avgRating = reviewService.getAvgRating(loginUser.getMemIdx());

	    model.addAttribute("reviewList", list);
	    model.addAttribute("receivedReviewList", receivedReviewList);
	    model.addAttribute("avgRating", avgRating);
	    
	    return "views/review/review"; 
	}
	
	
	// 리뷰 작성창 -----------------------------------------------------------------
	
	
	// 검색 전에도 기본 리스트가 table에 뜨도록 추가
	@GetMapping("/mypage/reviews/reviewWrite")
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
	@GetMapping("/mypage/reviews/reviewSearch")
	public String reviewSearch(@RequestParam(value="searchType", required = false) String searchType, 
							  @RequestParam(value="keyword", required = false) String keyword, 
							  HttpSession session,
							  Model model) {
		
		MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
		
		if (keyword != null && !keyword.trim().isEmpty()) {
	        List<ReviewVO> list = reviewService.search(searchType, keyword, loginUser.getMemIdx());
	        model.addAttribute("reviewList", list);
	        model.addAttribute("keyword", keyword);
	    } else {
	        model.addAttribute("keyword", null);
	    }
		
		return "views/review/reviewWrite";  
	}
	
	 
	// 리뷰 글쓰기 -----------------------------------------------------------------
	
	@PostMapping("/mypage/reviews/reviewWrite")
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

	    return "redirect:/mypage/reviews";
	}
	
	// 리뷰 상세보기 -----------------------------------------------------------------
	
	@GetMapping("/mypage/reviews/reviewDetail")
	public String reviewDetail(@RequestParam("reviewIdx") Long reviewIdx, Model model) {
		
		ReviewVO review = reviewService.getReviewDetail(reviewIdx);
		
		// 리뷰 내용 부르기
		model.addAttribute("review", review);
		
		return "/views/review/reviewDetail";
	}
	
	// 리뷰 삭제하기(관리자만) -----------------------------------------------------------------
	
	// 임시 삭제
	@GetMapping("/reviewDelete")
	public String reviewDelete(@RequestParam("reviewIdx") Long reviewIdx, HttpSession session) {

	    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");

	    if (loginUser == null || loginUser.getMemRoleIdx() != 2) {
	        return "redirect:/main";
	    }

	    // 삭제
	    reviewService.deleteReview(reviewIdx);

	    return "redirect:/reviewAdmin"; 
	}
	
	// 영구 삭제
	@GetMapping("/review/hardDelete")
	public String hardDelete(@RequestParam("reviewIdx") Long reviewIdx, HttpSession session) {

	    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");

	    if (loginUser == null || loginUser.getMemRoleIdx() != 2) {
	        return "redirect:/main";
	    }

	    reviewService.hardDeleteReview(reviewIdx);
	    return "redirect:/reviewAdmin";
	}
	
	// 삭제 취소 -----------------------------------------------------------------
	
	@GetMapping("/review/reviewCancel")
	public String reviewCancel(@RequestParam("reviewIdx") Long reviewIdx, HttpSession session) {
		
		MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");

	    if (loginUser == null || loginUser.getMemRoleIdx() != 2) {
	        return "redirect:/main";
	    }
	    
	    // 리뷰 취소 기능
	    reviewService.cancelDelete(reviewIdx);
		
		return "views/review/reviewAdmin"; 
	}
	
	// 관리자 리뷰 페이지 -----------------------------------------------------------------
	
	// 페이지만 띄움
	@GetMapping("/reviewAdmin")
	public String reviewAdmin(HttpSession session, Model model) {
	    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
	    
	    // 관리자만 들어올 수 있음
	    // 로그인 안했거나 관리자 아니면 메인으로 강제 이동
	    if (loginUser == null || loginUser.getMemRoleIdx() != 2) {
	        return "redirect:/main"; 
	    }

	    // 전체 리뷰 가져오기
	    List<ReviewVO> activeList = reviewService.getActiveReviewList(); // N
	    List<ReviewVO> deletedList = reviewService.getDeletedReviewList(); // Y

	    model.addAttribute("activeList", activeList);
	    model.addAttribute("deletedList", deletedList);

	    return "views/review/reviewAdmin"; 
	}
	
}
