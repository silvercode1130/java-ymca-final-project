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
        bidMapper.insertBid(bidDto);
    }
}
