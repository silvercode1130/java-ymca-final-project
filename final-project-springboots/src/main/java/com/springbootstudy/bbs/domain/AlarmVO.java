package com.springbootstudy.bbs.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AlarmVO {

    private Long alarmIdx;
    private Long receiverIdx;
    private Long senderIdx;
    private Long auctionIdx;
    private Long bidIdx;
    private String alarmType;
    private String alarmContent;
    private String targetUrl;
    private LocalDateTime createdAt;
    private String isRead;
}
