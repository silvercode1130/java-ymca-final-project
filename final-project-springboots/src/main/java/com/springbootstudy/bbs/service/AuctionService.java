package com.springbootstudy.bbs.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.domain.AuctionListDTO;
import com.springbootstudy.bbs.mapper.AuctionMapper;

@Service
public class AuctionService {
	
	@Autowired
	private AuctionMapper auctionMapper;
	
	public List<AuctionListDTO> AuctionList() {
		
		// DB에서 리스트 가져오기
        List<AuctionListDTO> list = auctionMapper.auctionList();
        
        // 현재 시간 (계산 기준)
        LocalDateTime now = LocalDateTime.now();

        // 리스트를 하나씩 돌면서 '빈칸' 채우기
        for (AuctionListDTO dto : list) {
            
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

            // 남은 시간 계산: 마감일시 - 현재시간
            if (dto.getAuctionEndAt() != null) {
                LocalDateTime end = dto.getAuctionEndAt();
                Duration duration = Duration.between(now, end);
                
                long days = duration.toDays();
                long hours = duration.toHoursPart();
                long minutes = duration.toMinutesPart();

                if (duration.isNegative()) {
                    dto.setTimeDisplay("마감된 경매");
                } else if (days > 0) {
                    dto.setTimeDisplay(days + "일 " + hours + "시간 남음");
                } else if (hours > 0) {
                    dto.setTimeDisplay(hours + "시간 " + minutes + "분 남음");
                } else {
                    dto.setTimeDisplay(minutes + "분 후 마감!");
                }
            }
        }
		
        return list;
    }
	
}
