package com.springbootstudy.bbs.domain;

import java.util.Date;

import lombok.Data;

@Data
public class AuctionVO {
    private Long   auctionIdx;          // PK
    private Long   buyerIdx;            // FK → member.mem_idx
    private Long   itemIdx;             // FK → item.item_idx
    private String auctionTitle;
    private String auctionDesc;
    private Long   auctionTargetPrice;  // 희망가(최대)
    private Date   auctionStartAt;
    private Date   auctionEndAt;
    private Date   auctionDecisionDeadline;
    private Long   auctionWinningBidIdx; // FK → bid.bid_idx (null가능)
    private Integer auctionStatusIdx;    // FK → auction_status.auction_status_idx
    private Date   auctionRegdate;
}
