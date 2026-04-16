package com.springbootstudy.bbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.domain.AuctionDTO;
import com.springbootstudy.bbs.domain.BidDTO;
import com.springbootstudy.bbs.domain.OrdersVO;
import com.springbootstudy.bbs.mapper.OrdersMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdersService {
	
	@Autowired
	private OrdersMapper ordersMapper;

	// 1. 낙찰 시 주문 생성
	public OrdersVO createOrderOnWinner(AuctionDTO auction, BidDTO bid) {
		
		OrdersVO existing = ordersMapper.findByAuctionAndBid(auction.getAuctionIdx(), bid.getBidIdx());
		if (existing != null) {
			return existing;
		}

		OrdersVO order = new OrdersVO();
		order.setAuctionIdx(auction.getAuctionIdx());
		order.setBidIdx(bid.getBidIdx());
		order.setBuyerIdx(auction.getBuyerIdx());
		order.setSellerIdx(bid.getBidderIdx());
		order.setOrderAmount(bid.getBidPrice());
		order.setOrderStatus("CREATED"); // 결제 대기
		order.setIsSettled("N");

		ordersMapper.insertOrder(order);
		return order;
	}

	// 2-1. 결제 완료 시 상태 변화
	public void markOrderPaid(Long orderIdx) {
		ordersMapper.updateOrderPaid(orderIdx);
	}

	// 2-2. 배송 시작 시 상태 변화
	public void markOrderShipped(Long orderIdx) {
		ordersMapper.updateOrderShipped(orderIdx);
	}

	// 2-3. 구매 확정 시 상태 변화
	public void markOrderConfirmed(Long orderIdx) {
		ordersMapper.updateOrderConfirmed(orderIdx);
	}

	// 2-4. bid 기준으로 주문 찾기 (배송/수령확인에서 사용)
	public OrdersVO findByBidIdx(Long bidIdx) {
		return ordersMapper.findByBidIdx(bidIdx);
	}

	// 3-1. 내가 구매한 거래내역 조회
	public List<OrdersVO> getOrdersAsBuyer(Long buyerIdx) {
		return ordersMapper.findAllByBuyerIdx(buyerIdx);
	}

	// 3-2. 내가 판매한 거래내역 조회
	public List<OrdersVO> getOrdersAsSeller(Long sellerIdx) {
		return ordersMapper.findAllBySellerIdx(sellerIdx);
	}

	// 3-3. 내가 참여한 전체 거래내역 (구매 + 판매) 조회
	public List<OrdersVO> getMyOrders(Long memIdx) {
		return ordersMapper.findAllByMemberIdx(memIdx);
	}
}