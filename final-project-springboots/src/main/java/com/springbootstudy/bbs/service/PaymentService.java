package com.springbootstudy.bbs.service;

import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.springbootstudy.bbs.domain.PaymentVO;

public interface PaymentService {
  /**
   * 1. 포트원 서버로부터 실제 결제 정보를 조회 (검증)
   */
  IamportResponse<Payment> verifyPayment(String impUid) throws Exception;

  /**
   * 2. 결제 데이터를 검증하고 DB 저장 및 상태 업데이트를 한 번에 처리 (비즈니스 로직)
   * (이 안에서 Mapper의 insertPayment, updateStatus 등을 호출하게 됩니다)
   */
  boolean savePaymentAndTrackStatus(PaymentVO paymentVO, long actualPaidAmount) throws Exception;
}
