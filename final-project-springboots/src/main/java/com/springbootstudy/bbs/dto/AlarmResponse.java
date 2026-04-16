package com.springbootstudy.bbs.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmResponse {

    private Long alarmIdx;
    private Long receiverIdx;
    private Long senderIdx;
    private Long auctionIdx;
    private Long bidIdx;
    private String alarmType;
    private String alarmContent;
    private String targetUrl;
    private LocalDateTime createdAt;
}
