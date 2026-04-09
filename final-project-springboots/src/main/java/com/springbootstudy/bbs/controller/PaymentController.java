package com.springbootstudy.bbs.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.springbootstudy.bbs.domain.PaymentVO;
import com.springbootstudy.bbs.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
  private final PaymentService paymentService;

  /**
   * 결제 검증 및 DB 기록 요청 처리
   */
  @PostMapping("/verify")
  public ResponseEntity<?> verifyAndSave(@RequestBody PaymentVO paymentVO) {
    try {
      // 1. 포트원 서버에서 실제 결제된 정보를 조회 (보안 검증 핵심)
      IamportResponse<Payment> response = paymentService.verifyPayment(paymentVO.getImpUid());

      // 실제 포트원 서버에 기록된 결제 금액 추출
      long actualPaidAmount = response.getResponse().getAmount().longValue();

      // 2. 서비스 계층에서 [금액 비교 + DB 저장 + 상태 업데이트] 일괄 처리
      boolean isSuccess = paymentService.savePaymentAndTrackStatus(paymentVO, actualPaidAmount);

      if (isSuccess) {
        log.info("결제 성공 처리 완료: {}", paymentVO.getMerchantUid());
        return ResponseEntity.ok(Map.of("message", "결제 처리가 완료되었습니다."));
      } else {
        log.error("결제 검증 실패: 요청 금액과 실제 결제 금액 불일치");
        return ResponseEntity.badRequest().body("결제 금액 검증에 실패했습니다.");
      }
    } catch (Exception e) {
      log.error("결제 처리 중 서버 에러 발생", e);
      return ResponseEntity.internalServerError().body("서버 처리 중 오류가 발생했습니다.");
    }
  }
}
