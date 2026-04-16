package com.springbootstudy.bbs.service;

import java.time.LocalDateTime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.domain.AlarmVO;
import com.springbootstudy.bbs.dto.AlarmResponse;
import com.springbootstudy.bbs.mapper.AlarmMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmMapper alarmMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public AlarmResponse saveAndPushAlarm(AlarmVO alarm) {
        if (alarm.getCreatedAt() == null) {
            alarm.setCreatedAt(LocalDateTime.now());
        }

        alarmMapper.insertAlarm(alarm);

        AlarmResponse response = AlarmResponse.builder()
                .alarmIdx(alarm.getAlarmIdx())
                .receiverIdx(alarm.getReceiverIdx())
                .senderIdx(alarm.getSenderIdx())
                .auctionIdx(alarm.getAuctionIdx())
                .bidIdx(alarm.getBidIdx())
                .alarmType(alarm.getAlarmType())
                .alarmContent(alarm.getAlarmContent())
                .targetUrl(alarm.getTargetUrl())
                .createdAt(alarm.getCreatedAt())
                .build();

        messagingTemplate.convertAndSend("/topic/alarm/" + alarm.getReceiverIdx(), response);
        return response;
    }
}
