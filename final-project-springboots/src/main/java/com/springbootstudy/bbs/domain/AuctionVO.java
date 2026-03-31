package com.springbootstudy.bbs.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AuctionVO {
  private Long auctionIdx;
  private Long buyerIdx;
  private Integer itemCategoryIdx;
  private String auctionTitle;
  private String auctionDesc;
  private Long auctionTargetPrice;
  private Long auctionViewCount;
  private LocalDateTime auctionStartAt;
  private LocalDateTime auctionEndAt;
  private LocalDateTime auctionDecisionDeadline;
  private Integer auctionStatusIdx;
  private LocalDateTime auctionRegdate;
  private LocalDateTime auctionModdate;
  private String auctionIsDeleted;
  private LocalDateTime auctionDeldate;
  private int bidCount;

  // 조인(Join) 결과를 담기 위한 추가 필드 (HTML에서 사용)
  private String auctionStatusName; // 예: 진행중, 마감
  private String auctionStatusCode; // 예: open, closed
}