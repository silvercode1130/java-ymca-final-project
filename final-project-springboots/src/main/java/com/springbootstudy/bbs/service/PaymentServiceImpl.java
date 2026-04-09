package com.springbootstudy.bbs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.springbootstudy.bbs.domain.PaymentVO;
import com.springbootstudy.bbs.mapper.PaymentMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
  private final PaymentMapper paymentMapper;
  private IamportClient api;

  // 포트원 관리자 페이지에서 확인한 API 키와 시크릿을 여기에 넣습니다.
  @PostConstruct
  public void init() {
    this.api = new IamportClient("6586607241723066",
        "kTuccbRmJs5zqZDLJrnej9G3S0aiJwbtKrNuF49creXbBKfW6mTFCShuhN1vUWTpFZFSSL5gO1EwT3kg");
  }

  @Override
  public IamportResponse<Payment> verifyPayment(String impUid) throws Exception {
    // 포트원 서버에 직접 결제 정보를 요청하여 받아옵니다.
    return api.paymentByImpUid(impUid);
  }

  @Override
  @Transactional // 하나라도 실패하면 롤백!
  public boolean savePaymentAndTrackStatus(PaymentVO paymentVO, long actualPaidAmount) throws Exception {
    // 1. 금액 검증 (낙찰가와 실제 결제금액이 다른지 확인)
    if (paymentVO.getPayAmount() != actualPaidAmount) {
      return false; // 금액 위변조 가능성 있음
    }

    // 2. 결제 내역 저장 (insert)
    int result = paymentMapper.insertPayment(paymentVO);

    if (result > 0) {
      // 3. 경매 상태 변경 (8: 결제완료)
      paymentMapper.updateAuctionStatus(paymentVO.getAuctionIdx(), 8);
      // 4. 입찰 상태 변경 (2: 낙찰/결제성공)
      paymentMapper.updateBidStatus(paymentVO.getBidIdx(), 2);
      return true;
    }
    return false;
  }
}
