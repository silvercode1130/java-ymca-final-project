package com.springbootstudy.bbs.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.springbootstudy.bbs.domain.AlarmVO;
import com.springbootstudy.bbs.service.AlarmService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AlarmWebSocketController {

    private final AlarmService alarmService;

    @MessageMapping("/alarm/send")
    public void sendAlarm(@Payload AlarmVO alarm) {
        alarmService.saveAndPushAlarm(alarm);
    }
}
