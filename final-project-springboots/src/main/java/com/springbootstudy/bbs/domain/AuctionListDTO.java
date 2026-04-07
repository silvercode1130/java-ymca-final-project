package com.springbootstudy.bbs.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuctionListDTO extends BidVO{
	// BidVO의 모든 필드(bidPrice, bidRegdate 등)는 상속받음
    
    // Auction 관련 (기본 정보)
    private Long auctionIdx;
    private String auctionTitle;
    private Long auctionTargetPrice;
    private LocalDateTime auctionEndAt;  // 입찰 마감일시
    private String auctionDesc;			
    private LocalDateTime 	auctionDecisionDeadline;  // 결정 마감일
    private Integer auctionStatusIdx;
    private String  auctionStatusName;  // 한글 상태명 (진행중/마감/유찰/취소 등)
    
    // Item 관련 (이미지)
    private String auctionThumbnailImg;  // 이미지 - auction 테이블에 직접 저장
    private String itemBrand;  // 브랜드 칸
    private String itemName;
    private String itemThumbnailImg;  // 입찰 제안 이미지
    
    // ItemCategory 관련 (카테고리 번호, 이름)
    private Integer itemCategoryIdx;
    private String itemCategoryName;
    
    // Bid 관련 (계산이 필요한 정보들 - 쿼리에서 가져올 것)
    private Long minBidPrice;  // 최저 제안가
    private Integer bidCount;  // 제안 건수
    
    // Service 에서 계산해서 채워줄 필드 (가공)
    private int discountRate;    // "13% 절약"
    private String timeDisplay;  // "1시간 45분"
    private String statusBadge;  // "마감 임박"
    
    // member 관련
    // MemberVO에서 가져올 이름 (익명 처리용)
    private Long buyerIdx;    		// 작성자(구매자) 번호
	private String memName;         // 마스킹된 이름
    private String realMemName;     // 실명 (구매자에게만 표시)
    
    // bid_status 테이블에서 가져올 한글 상태명 (일반, 낙찰 등)
    private String bidStatusName; 
    
}