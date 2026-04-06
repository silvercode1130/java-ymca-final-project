package com.springbootstudy.bbs.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springbootstudy.bbs.domain.AuctionListDTO;
import com.springbootstudy.bbs.mapper.AuctionMapper;

@Service
public class AuctionService {
	
	@Autowired
	private AuctionMapper auctionMapper;
	
	// 경매 구매요청 전체 리스트 조회
	public List<AuctionListDTO> AuctionList() {
		
		// DB에서 리스트 가져오기
        List<AuctionListDTO> list = auctionMapper.auctionList();
        
        // 리스트를 하나씩 돌면서 '빈칸' 채우기
        for (AuctionListDTO dto : list) {
        	refine(dto);
        }
		
        return list;
    }
	
	// 구매요청 상세보기 조회
	public AuctionListDTO auctionDetail(Long auctionIdx) {
		
		AuctionListDTO detail = auctionMapper.auctionDetail(auctionIdx);
		
		if(detail != null) {
			refine(detail);
		}
		
		return detail;
	}
	
	
	
	// 절약율 계산과 남은 시간 계산을 도와주는 메서드 
	private void refine(AuctionListDTO dto) {
		
		// 절약율 계산: (희망가 - 최저가) / 희망가 * 100
        if (dto.getAuctionTargetPrice() > 0 && dto.getMinBidPrice() > 0) {
            double target = dto.getAuctionTargetPrice();
            double minPrice = dto.getMinBidPrice();
            
            // 0으로 나누기 방지 및 정수형 반올림 계산
            int rate = (int) Math.round(((target - minPrice) / target) * 100);
            dto.setDiscountRate(rate); 
        } 
        else {
            dto.setDiscountRate(0); // 입찰이 없으면 0%
        }
        

        // 현재 시간 (계산 기준)
        LocalDateTime now = LocalDateTime.now();

        // 남은 시간 계산: 마감일시 - 현재시간
        if (dto.getAuctionEndAt() != null) {
            LocalDateTime end = dto.getAuctionEndAt();
            Duration duration = Duration.between(now, end);
            
            long days = duration.toDays();
            long hours = duration.toHoursPart();
            long minutes = duration.toMinutesPart();

            if (duration.isNegative()) {
                dto.setTimeDisplay("마감된 경매");
            } 
            else if (days > 0) {
                dto.setTimeDisplay(days + "일 " + hours + "시간 남음");
            } 
            else if (hours > 0) {
                dto.setTimeDisplay(hours + "시간 " + minutes + "분 남음");
            } 
            else {
                dto.setTimeDisplay(minutes + "분 후 마감!");
            }
        }
	}
	
	@Transactional
	public void registerAuction(AuctionListDTO dto) {

	    // 희망 최대가 검증
	    if (dto.getAuctionTargetPrice() == null || dto.getAuctionTargetPrice() <= 0) {
	        throw new IllegalArgumentException("희망 최대가는 0원보다 커야 합니다.");
	    }
	    if (dto.getAuctionTargetPrice() % 1000 != 0) {
	        throw new IllegalArgumentException("희망 최대가는 1000원 단위로 입력해야 합니다.");
	    }

	    // 입찰 마감일 검증
	    LocalDateTime now = LocalDateTime.now();
	    if (dto.getAuctionEndAt() == null || dto.getAuctionEndAt().isBefore(now)) {
	        throw new IllegalArgumentException("입찰 마감일은 현재 시간 이후여야 합니다.");
	    }

	    // 결정 마감일 검증
	    if (dto.getAuctionDecisionDeadline() == null || dto.getAuctionDecisionDeadline().isBefore(now)) {
	        throw new IllegalArgumentException("결정 마감일은 현재 시간 이후여야 합니다.");
	    }
	    if (dto.getAuctionDecisionDeadline().isBefore(dto.getAuctionEndAt())) {
	        throw new IllegalArgumentException("결정 마감일은 입찰 마감일 이후여야 합니다.");
	    }
	    LocalDateTime maxDeadline = dto.getAuctionEndAt().plusDays(3);
	    if (dto.getAuctionDecisionDeadline().isAfter(maxDeadline)) {
	        throw new IllegalArgumentException("결정 마감일은 입찰 마감일로부터 3일을 초과할 수 없습니다.");
	    }
	    
	    
	    
	    // DB 저장
	    auctionMapper.insertItem(dto);
	    auctionMapper.insertAuction(dto);
	}
	
	// =====================================================
	// 4번: 마감 경매 상태 자동 업데이트
	// 입찰 마감일 지난 경매:
	//   - 입찰 있으면 → 2 (마감)
	//   - 입찰 없으면 → 3 (유찰)
	// =====================================================
	@Transactional
	public void updateExpiredAuctions() {
	    List<AuctionListDTO> expiredList = auctionMapper.findExpiredAuctions();

	    for (AuctionListDTO dto : expiredList) {
	        // bidCount가 null이면 0으로 처리
	        int bidCount = dto.getBidCount() != null ? dto.getBidCount() : 0;

	        if (bidCount > 0) {
	            // 입찰 있음 → 마감 (2)
	            auctionMapper.updateAuctionStatus(dto.getAuctionIdx(), 2);
	        } else {
	            // 입찰 없음 → 유찰 (3)
	            auctionMapper.updateAuctionStatus(dto.getAuctionIdx(), 3);
	        }
	    }
	}
	
	// 경매 삭제 - 소프트 딜리트
	@Transactional
	public void deleteAuction(Long auctionIdx, Long buyerIdx) {
	    int result = auctionMapper.softDeleteAuction(auctionIdx, buyerIdx);
	    if (result == 0) {
	        throw new IllegalArgumentException("삭제 권한이 없거나 존재하지 않는 경매입니다.");
	    }
	}
	
	// 경매 수정
	@Transactional
	public void updateAuction(AuctionListDTO dto) {
	    // 희망 최대가 검증
	    if (dto.getAuctionTargetPrice() == null || dto.getAuctionTargetPrice() <= 0) {
	        throw new IllegalArgumentException("희망 최대가는 0원보다 커야 합니다.");
	    }
	    if (dto.getAuctionTargetPrice() % 100 != 0) {
	        throw new IllegalArgumentException("희망 최대가는 100원 단위로 입력해야 합니다.");
	    }
	    LocalDateTime now = LocalDateTime.now();
	    if (dto.getAuctionEndAt() == null || dto.getAuctionEndAt().isBefore(now)) {
	        throw new IllegalArgumentException("입찰 마감일은 현재 시간 이후여야 합니다.");
	    }
	    if (dto.getAuctionDecisionDeadline() == null || dto.getAuctionDecisionDeadline().isBefore(now)) {
	        throw new IllegalArgumentException("결정 마감일은 현재 시간 이후여야 합니다.");
	    }
	    if (dto.getAuctionDecisionDeadline().isBefore(dto.getAuctionEndAt())) {
	        throw new IllegalArgumentException("결정 마감일은 입찰 마감일 이후여야 합니다.");
	    }
	    LocalDateTime maxDeadline = dto.getAuctionEndAt().plusDays(3);
	    if (dto.getAuctionDecisionDeadline().isAfter(maxDeadline)) {
	        throw new IllegalArgumentException("결정 마감일은 입찰 마감일로부터 3일을 초과할 수 없습니다.");
	    }

	    int result = auctionMapper.updateAuction(dto);
	    if (result == 0) {
	        throw new IllegalArgumentException("수정 권한이 없거나 수정할 수 없는 상태입니다.");
	    }
	}
	
}
