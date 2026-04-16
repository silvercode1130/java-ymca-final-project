package com.springbootstudy.bbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springbootstudy.bbs.domain.AuctionDTO;
import com.springbootstudy.bbs.domain.BidDTO;
import com.springbootstudy.bbs.domain.OrdersVO;
import com.springbootstudy.bbs.dto.OrdersListDTO;
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
	@Transactional
	public void markOrderPaid(Long orderIdx) {
		OrdersVO order = requireOrder(orderIdx);
		if (!"CREATED".equals(order.getOrderStatus()) && !"PAID".equals(order.getOrderStatus())) {
			throw new IllegalStateException("CREATED 상태에서만 결제완료 처리할 수 있습니다.");
		}
		ordersMapper.updateOrderPaid(orderIdx);
	}

	// 2-2. 배송 시작 시 상태 변화
	@Transactional
	public void markOrderShipped(Long orderIdx) {
		OrdersVO order = requireOrder(orderIdx);
		if (!"PAID".equals(order.getOrderStatus()) && !"SHIPPED".equals(order.getOrderStatus())) {
			throw new IllegalStateException("PAID 상태에서만 배송시작 처리할 수 있습니다.");
		}
		ordersMapper.updateOrderShipped(orderIdx);
	}

	// 2-3. 구매 확정 시 상태 변화
	@Transactional
	public void markOrderConfirmed(Long orderIdx) {
		OrdersVO order = requireOrder(orderIdx);
		if (!"SHIPPED".equals(order.getOrderStatus()) && !"CONFIRMED".equals(order.getOrderStatus())) {
			throw new IllegalStateException("SHIPPED 상태에서만 구매확정 처리할 수 있습니다.");
		}
		ordersMapper.updateOrderConfirmed(orderIdx);
	}

	// 2-4. bid 기준으로 주문 찾기 (배송/수령확인에서 사용)
	public OrdersVO findByBidIdx(Long bidIdx) {
		return ordersMapper.findByBidIdx(bidIdx);
	}

	public OrdersVO findByOrderIdx(Long orderIdx) {
		return ordersMapper.findByOrderIdx(orderIdx);
	}

	public OrdersListDTO getOrderDetailForMember(Long orderIdx, Long memIdx) {
		return ordersMapper.findDetailByOrderIdxAndMember(orderIdx, memIdx);
	}

	// 3-1. 내가 구매한 거래내역 조회
	public List<OrdersListDTO> getOrdersAsBuyer(Long buyerIdx) {
		return ordersMapper.findAllByBuyerIdx(buyerIdx);
	}

	// 3-2. 내가 판매한 거래내역 조회
	public List<OrdersListDTO> getOrdersAsSeller(Long sellerIdx) {
		return ordersMapper.findAllBySellerIdx(sellerIdx);
	}

	// 3-3. 내가 참여한 전체 거래내역 (구매 + 판매) 조회
	public List<OrdersListDTO> getMyOrders(Long memIdx) {
		return ordersMapper.findAllByMemberIdx(memIdx);
	}

	private OrdersVO requireOrder(Long orderIdx) {
		OrdersVO order = ordersMapper.findByOrderIdx(orderIdx);
		if (order == null) {
			throw new IllegalArgumentException("주문 정보를 찾을 수 없습니다.");
		}
		return order;
	}
}