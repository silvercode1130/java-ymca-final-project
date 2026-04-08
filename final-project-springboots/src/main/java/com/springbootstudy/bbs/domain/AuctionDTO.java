package com.springbootstudy.bbs.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuctionDTO {

    // Auction 기본 정보
    private Long auctionIdx;
    private String auctionTitle;
    private Long auctionTargetPrice;
    private LocalDateTime auctionEndAt;
    private String auctionDesc;
    private LocalDateTime auctionDecisionDeadline;
    private LocalDateTime auctionRegdate; // 리스트 정렬 및 날짜 표시용

    private Integer auctionStatusIdx;
    private String  auctionStatusCode;
    private String auctionStatusName;

    // 카테고리
    private Integer itemCategoryIdx;
    private String itemCategoryName;
    private String itemCategoryCode;   // URL용 카테고리 코드 (ball, racket 등)

    // 이미지
    private String auctionThumbnailImg;

    // 입찰 집계 (리스트용 최소 정보)
    private Long minBidPrice;
    private Integer bidCount;

    // 작성자
    private Long buyerIdx;

    // 서비스 가공 데이터
    // private int discountRate;
    private String timeDisplay;
    private String statusBadge;
}