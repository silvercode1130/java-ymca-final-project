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
  public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> requestData) {
    try {
      // 1. 파라미터 체크 (필수값이 없으면 즉시 차단)
      if (requestData.get("paymentKey") == null || requestData.get("orderId") == null) {
        return ResponseEntity.badRequest().body("필수 결제 정보가 누락되었습니다.");
      }

      String paymentKey = (String) requestData.get("paymentKey");
      String orderId = (String) requestData.get("orderId");
      Long amount = Long.valueOf(String.valueOf(requestData.get("amount")));

      // 2. 토스 서버로 승인 요청
      Map<String, Object> tossResponse = paymentService.confirmPayment(paymentKey, orderId, amount);

      // 3. VO 세팅 (토스 응답 + 요청 데이터 결합)
      PaymentVO paymentVO = new PaymentVO();
      paymentVO.setPaymentKey(paymentKey);
      paymentVO.setOrderId(orderId);
      paymentVO.setPayAmount(amount);
      paymentVO.setBidIdx(Long.valueOf(String.valueOf(requestData.get("bidIdx"))));
      paymentVO.setMemIdx(Long.valueOf(String.valueOf(requestData.get("memIdx"))));

      // 결제 수단이 null일 경우를 대비
      String method = (String) tossResponse.get("method");
      paymentVO.setPayMethod(method != null ? method : "UNKNOWN");

      // 배송지 스냅샷 세팅 (프론트에서 반드시 보내줘야 함)
      paymentVO.setBuyerName((String) requestData.getOrDefault("buyerName", "구매자"));
      paymentVO.setBuyerTel((String) requestData.getOrDefault("buyerTel", "010-0000-0000"));
      paymentVO.setBuyerAddr((String) requestData.getOrDefault("buyerAddr", "정보 없음"));
      paymentVO.setBuyerZipcode((String) requestData.getOrDefault("buyerZipcode", "00000"));

      // 4. DB 처리 및 트랜잭션 실행
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