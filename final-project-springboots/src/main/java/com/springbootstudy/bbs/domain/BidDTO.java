package com.springbootstudy.bbs.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BidDTO {

    // Bid 기본 정보
    private Long bidIdx;          // 입찰 번호 (PK)
    private Long auctionIdx;      // 경매 번호 (FK)
    private Long bidderIdx;       // 입찰자 회원 번호 (FK)

    // 입찰 내용
    private Long bidPrice;        // 제안 가격
    private Integer bidQuantity;
    private String bidMessage;    // 상세페이지에서 사용
    
    // 입찰 상태
    private Integer bidStatusIdx;     // 상태 코드 (1: 일반, 2: 낙찰, 3: 실패, 4: 삭제)
    private String bidStatusName;     // 상태 이름 (한글)
    
    // 입찰자 이름 (3가지 용도로 분리)
    private String memName;       // 뷰에서 가져온 실명 → 구매자에게 보여줄 때 (bid.memName)
    private String realMemName;   // 뷰에서 가져온 실명 → 입찰 상세에서 사용 (bid.realMemName)
    private String bidderName;    // 서비스에서 마스킹해서 채워줌 → 일반 사용자에게 보여줄 때 (bid.bidderName)

    // 아이템 정보 (상세페이지용)
    private Long itemIdx;
    private String itemName;
    private String itemBrand;
    private String itemThumbnailImg;
    private String itemCategoryName;

    // 시간 정보
    private LocalDateTime bidRegdate; // 입찰 등록일
    
}