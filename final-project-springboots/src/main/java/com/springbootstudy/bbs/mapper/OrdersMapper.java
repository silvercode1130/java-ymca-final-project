package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.springbootstudy.bbs.domain.OrdersVO;

@Mapper
public interface OrdersMapper {

    // 낙찰된 특정 경매+입찰 조합으로 주문 한 건 조회
    OrdersVO findByAuctionAndBid(Long auctionIdx, Long bidIdx);

    // bid 기준으로 주문 한 건 조회 (배송/수령확인에서 사용)
    OrdersVO findByBidIdx(Long bidIdx);

    // 주문 생성 (낙찰 직후)
    int insertOrder(OrdersVO order);

    // 상태 업데이트용
    int updateOrderPaid(Long orderIdx);

    int updateOrderShipped(Long orderIdx);

    int updateOrderConfirmed(Long orderIdx);

    // 구매자로 참여한 거래 전체 조회
    List<OrdersVO> findAllByBuyerIdx(Long buyerIdx);

    // 판매자로 참여한 거래 전체 조회
    List<OrdersVO> findAllBySellerIdx(Long sellerIdx);

    // 구매자/판매자 둘 다 포함한 내 거래내역
    List<OrdersVO> findAllByMemberIdx(Long memIdx);
}