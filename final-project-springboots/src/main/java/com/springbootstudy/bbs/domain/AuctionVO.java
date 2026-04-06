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

}