package com.springbootstudy.bbs.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springbootstudy.bbs.domain.PaymentVO;
import com.springbootstudy.bbs.service.PaymentService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentApiController {

    private final PaymentService paymentService;

    /**
     * 토스페이먼츠 결제 승인 및 최종 처리
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> requestData,
            HttpSession session) {
        try {
            // memIdx는 URL 노출 없이 세션에서 꺼내기
            Long memIdx = (Long) session.getAttribute("memIdx");
            if (memIdx == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            String paymentKey = (String) requestData.get("paymentKey");
            String orderId = (String) requestData.get("orderId");
            Long amount = Long.valueOf(String.valueOf(requestData.get("amount")));
            Long bidIdx = Long.valueOf(String.valueOf(requestData.get("bidIdx")));

            Map<String, Object> tossResponse = paymentService.confirmPayment(paymentKey, orderId, amount);

            PaymentVO paymentVO = new PaymentVO();
            paymentVO.setPaymentKey(paymentKey);
            paymentVO.setOrderId(orderId);
            paymentVO.setPayAmount(amount);
            paymentVO.setBidIdx(bidIdx);
            paymentVO.setMemIdx(memIdx); // 세션에서 꺼낸 값

            String method = (String) tossResponse.get("method");
            paymentVO.setPayMethod(method != null ? method : "UNKNOWN");

            paymentVO.setBuyerName((String) requestData.getOrDefault("buyerName", "구매자"));
            paymentVO.setBuyerTel((String) requestData.getOrDefault("buyerTel", "010-0000-0000"));
            paymentVO.setBuyerAddr((String) requestData.getOrDefault("buyerAddr", "정보 없음"));
            paymentVO.setBuyerZipcode((String) requestData.getOrDefault("buyerZipcode", "00000"));

            boolean isSuccess = paymentService.savePaymentAndTrackStatus(paymentVO, tossResponse);

            if (isSuccess) {
                return ResponseEntity.ok(Map.of("message", "success", "orderId", orderId));
            } else {
                return ResponseEntity.badRequest().body("결제 검증 및 DB 기록 실패");
            }

        } catch (Exception e) {
            log.error("결제 처리 중 에러: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}