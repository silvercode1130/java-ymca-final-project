package com.springbootstudy.bbs.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BidVO {
  private Long bidIdx;
  private Long auctionIdx;
  private Long bidderIdx;
  private Long itemIdx;
  private Long bidPrice;
  private Integer bidQuantity;
  private String bidMessage;
  private Integer bidStatusIdx;
  private LocalDateTime bidRegdate;
  private LocalDateTime bidModdate;
  private String auctionTitle;

  // 조인(Join) 결과를 담기 위한 추가 필드
  private String bidStatusName; // 예: 낙찰, 입찰중
  private String bidStatusCode; // 예: won, normal
}