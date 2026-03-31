package com.springbootstudy.bbs.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BidListDTO extends BidVO {
    // BidVO의 모든 필드(bidPrice, bidRegdate 등)는 상속받음
    
    // MemberVO에서 가져올 이름 (익명 처리용)
    private String memName; 
    
    // bid_status 테이블에서 가져올 한글 상태명 (일반, 낙찰 등)
    private String bidStatusName; 
}