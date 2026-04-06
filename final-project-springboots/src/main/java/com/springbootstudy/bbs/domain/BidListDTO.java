package com.springbootstudy.bbs.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BidListDTO extends BidVO {
    // BidVO의 모든 필드(bidPrice, bidRegdate 등)는 상속받음
    
    // MemberVO에서 가져올 이름 (익명 처리용)
	private String memName;         // 마스킹된 이름
    private String realMemName;     // 실명 (구매자에게만 표시)
    
    // bid_status 테이블에서 가져올 한글 상태명 (일반, 낙찰 등)
    private String bidStatusName; 
    
    private Long itemIdx;
    private Integer itemCategoryIdx;
    private String itemThumbnailImg; // 입찰 제안 이미지
}