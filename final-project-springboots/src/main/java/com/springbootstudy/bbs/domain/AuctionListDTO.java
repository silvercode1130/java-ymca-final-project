package com.springbootstudy.bbs.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuctionListDTO {
    // Auction 관련 (기본 정보)
    private Long auctionIdx;
    private String auctionTitle;
    private Long auctionTargetPrice;
    private LocalDateTime auctionEndAt;  // 입찰 마감일시
    private String auctionDesc;			
    private LocalDateTime 	auctionDecisionDeadline;// 결정 마감일
    private Integer auctionStatusIdx;
    private String  auctionStatusName;  // 한글 상태명 (진행중/마감/유찰/취소 등)
    
    // Item 관련 (이미지)
    private String itemThumbnailImg;
    private Long itemIdx;     // 아이템 PK (저장할 때 필요)
    private String itemBrand; // 브랜드 칸
    
    // ItemCategory 관련 (카테고리 번호, 이름)
    private Integer itemCategoryIdx;
    private String itemCategoryName;
    
    // Bid 관련 (계산이 필요한 정보들 - 쿼리에서 가져올 것)
    private Long minBidPrice; // 최저 제안가
    private Integer bidCount; // 제안 건수
    
    // Service 에서 계산해서 채워줄 필드 (가공)
    private int discountRate;    // "13% 절약"
    private String timeDisplay;  // "1시간 45분"
    private String statusBadge;  // "마감 임박"
    
    // member 관련
    private Long buyerIdx;    // 작성자(구매자) 번호
    
    // err 땜에 임시로 추가 -> 마이 페이지 클릭 시 에러남
    private String auctionStatusCode;
    
    
}