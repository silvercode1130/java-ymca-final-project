package com.springbootstudy.bbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.domain.BidListDTO;
import com.springbootstudy.bbs.mapper.BidMapper;


@Service
public class BidService {
	
	@Autowired
	private BidMapper bidMapper;
	
	// 입찰 리스트 조회
    public List<BidListDTO> BidList(Long auctionIdx) {
        List<BidListDTO> list = bidMapper.BidList(auctionIdx);
        
        for (BidListDTO dto : list) {
            String name = dto.getMemName();
            if (name != null && name.length() > 1) {
                // 첫 글자 빼고 다 별표! (신정은 -> 신**)
                String masked = name.substring(0, 1) + "*".repeat(name.length() - 1);
                dto.setMemName(masked);
            }
        }
        return list;
    }
    
    // 입찰 등록
    public void registerBid(BidListDTO bidDto) {

        // 입찰가 검증 - 음수/0 방지
        if (bidDto.getBidPrice() == null || bidDto.getBidPrice() <= 0) {
            throw new IllegalArgumentException("제안 가격은 0원보다 커야 합니다.");
        }
        // 100원 단위 검증
        if (bidDto.getBidPrice() % 100 != 0) {
            throw new IllegalArgumentException("제안 가격은 100원 단위로 입력해야 합니다.");
        }

        bidMapper.insertBid(bidDto);
    }
}
