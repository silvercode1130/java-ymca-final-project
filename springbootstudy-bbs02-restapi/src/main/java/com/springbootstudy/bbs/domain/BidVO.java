package com.springbootstudy.bbs.domain;

import java.util.Date;

import lombok.Data;

@Data
public class BidVO {
    private Long   bidIdx;        // PK
    private Long   auctionIdx;    // FK → auction.auction_idx
    private Long   bidderIdx;     // FK → member.mem_idx
    private Long   bidPrice;
    private Integer bidQuantity;
    private String bidMessage;
    private Integer bidStatusIdx; // FK → bid_status.bid_status_idx
    private Date   bidRegdate;
}

