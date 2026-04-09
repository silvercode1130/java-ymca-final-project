package com.springbootstudy.bbs.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.springbootstudy.bbs.domain.PaymentVO;

@Mapper
public interface PaymentMapper {
  /**
   * 결제 정보 저장
   * 
   * @param payment 결제 상세 정보 (스냅샷 포함)
   * @return 영향받은 행의 수
   */
  int insertPayment(PaymentVO payment);

  /**
   * 경매 상태 업데이트
   * 
   * @param auctionIdx 경매 번호
   * @param statusIdx  상태 코드 (8: 결제완료 등)
   * @return 영향받은 행의 수
   */
  int updateAuctionStatus(@Param("auctionIdx") Long auctionIdx, @Param("statusIdx") int statusIdx);

  /**
   * 입찰 상태 업데이트
   * 
   * @param bidIdx    입찰 번호
   * @param statusIdx 상태 코드 (2: 낙찰/결제완료 등)
   * @return 영향받은 행의 수
   */
  int updateBidStatus(@Param("bidIdx") Long bidIdx, @Param("statusIdx") int statusIdx);
}
