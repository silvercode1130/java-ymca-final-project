package com.springbootstudy.bbs.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PaymentVO {
  private Long payIdx;
  private Long auctionIdx;
  private Long memIdx;
  private Long bidIdx;

  private String impUid; // 포트원 고유 번호
  private String merchantUid; // 주문 번호
  private String payMethod; // 결제 수단
  private Long payAmount; // 결제 금액
  private String payStatus; // 결제 상태

  // 기록용 (결제 시점의 스냅샷)
  private String buyerName; // MemberVO.memName 복사
  private String buyerTel; // MemberVO.memTel 복사
  private String buyerAddr; // MemberAddrVO.memAddr + 상세주소 복사
  private String buyerZipcode; // MemberAddrVO.memZipcode 복사

  private LocalDateTime payRegdate;
  private LocalDateTime payPaidAt;
}
