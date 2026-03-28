package com.springbootstudy.bbs.domain;

import java.time.LocalDateTime;

import lombok.Data;

//입찰(판매자 제안)
@Data
public class BidVO {

 private Long    		bidIdx;           // PK
 private Long    		auctionIdx;       // FK → auction
 private Long    		bidderIdx;        // FK → member
 private Long    		itemIdx;          // FK → item (실제 제안 상품)
 private Long    		bidPrice;         // 제안 가격
 private Integer 		bidQuantity;      // 수량
 private String  		bidMessage;       // 제안 조건/설명
 private Integer 		bidStatusIdx;     // FK → bid_status (NORMAL/WON/LOST/CANCELED)
 private LocalDateTime 	bidRegdate;		  // 등록일
 private LocalDateTime 	bidModdate;       // 수정/취소일 추적
}

