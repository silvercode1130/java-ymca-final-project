package com.springbootstudy.bbs.domain;

import java.time.LocalDateTime;

import lombok.Data;

// 임시 VO 입니다
// DB 확정 전이라 사라질 수 있어요!

@Data
public class ReviewVO {
	private Long reviewIdx;	// pk - 리뷰
	
	private Long buyer_idx;   // fk - 구매자(리뷰하는 자)
	private Long bidder_idx;  // fk - 입찰자(리뷰 대상자)
	private Long auction_idx; // fk - 역경매(해당 경매)
	private Long bidIdx; 	  // fk - 입찰(해당 입찰)
	
	private String reviewTitle;	  // 리뷰 제목
	private String reviewContent; // 리뷰 내용
	private int reviewStar;	  	  // 별점
	
	private LocalDateTime reviewRegdate; // 리뷰 작성일
	private String reviewIsDelete;		 // 리뷰 삭제 여부 - default N (N / Y)
	private LocalDateTime reviewDelete;  // 리뷰 삭제일
	
	// 리뷰 게시를 위한 추가 필드
	private String content;
	private String auctionTitle;
	private String itemName;
	private Long auctionIdx;
	private Long bidderIdx;
	
	public void setReviewStar(int reviewStar) { this.reviewStar = reviewStar; }  
	public void setContent(String content) { this.content = content; }
	public Long getBuyerIdx() { return buyer_idx; }
	public void setBuyerIdx(Long buyerIdx) { this.buyer_idx = buyerIdx; }  
	
}
