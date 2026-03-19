package com.springbootstudy.bbs.domain;

import java.time.LocalDateTime;

import lombok.Data;

//역경매 요청(구매자 기준 경매)
@Data
public class AuctionVO {

 private Long    		auctionIdx;				// PK
 private Long    		buyerIdx;				// FK → member.mem_idx
 private Integer 		itemCategoryIdx;		// FK → item_category
 private String  		auctionTitle;			// 경매 제목
 private String  		auctionDesc;			// 경매 설명
 private Long    		auctionTargetPrice;		// 희망 최대가 (nullable)
 private LocalDateTime 	auctionStartAt;			// 경매 시작일시
 private LocalDateTime 	auctionEndAt;			// 입찰 마감일시
 private LocalDateTime 	auctionDecisionDeadline;// 결정 마감일
 private Long    		auctionWinningBidIdx;	// FK → bid.bid_idx (nullable)
 private Integer 		auctionStatusIdx;		// FK → auction_status
 private LocalDateTime 	auctionRegdate;			// 등록일
 private Long    		auctionViewCount;		// 조회수

}
