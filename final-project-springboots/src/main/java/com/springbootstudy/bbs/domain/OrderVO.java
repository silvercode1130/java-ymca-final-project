package com.springbootstudy.bbs.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrderVO {

    private Long orderIdx;

    private Long auctionIdx;
    private Long bidIdx;

    private Long buyerIdx;
    private Long sellerIdx;

    private Long orderAmount;

    private String orderStatus;      // CREATED, PAID, SHIPPED, CONFIRMED, CANCELED ...
    private String paymentStatus;    // READY, PAID, REFUND, FAIL ...
    private String shippingStatus;   // NONE, READY, SHIPPED, DELIVERED, CONFIRMED ...

    private String trackingNumber;
    private String courierName;

    private String isSettled;        // 'Y' = 정산완료, 'N' = 아직 에스크로 보류

    private LocalDateTime paidAt;
    private LocalDateTime shippedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime refundAt;

    private LocalDateTime orderRegdate;
}
