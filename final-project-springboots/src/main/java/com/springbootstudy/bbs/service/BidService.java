package com.springbootstudy.bbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                // 판매자 이름 가리기 (신정은 -> 신**)
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
        if (bidDto.getBidPrice() % 1000 != 0) {
            throw new IllegalArgumentException("제안 가격은 1000원 단위로 입력해야 합니다.");
        }

        bidMapper.insertBid(bidDto);
    }
    
    // 입찰 삭제 - 소프트 딜리트
    public void deleteBid(Long bidIdx, Long bidderIdx) {
        int result = bidMapper.softDeleteBid(bidIdx, bidderIdx);
        if (result == 0) {
            throw new IllegalArgumentException("삭제 권한이 없거나 존재하지 않는 입찰입니다.");
        }
    }
    
    // 낙찰 처리 (구매자가 선택)
    @Transactional
    public void selectWinner(Long bidIdx, Long auctionIdx) {
        // 선택된 입찰 낙찰
        int result = bidMapper.selectWinnerBid(bidIdx, auctionIdx);
        if (result == 0) {
            throw new IllegalArgumentException("낙찰 처리에 실패했습니다.");
        }
        // 나머지 입찰 실패 처리
        bidMapper.rejectOtherBids(auctionIdx, bidIdx);
    }

    // 입찰 단건 조회
    public BidListDTO findBidById(Long bidIdx) {
        return bidMapper.findBidById(bidIdx);
    }

    // 입찰 수정
    public void updateBid(BidListDTO bidDto) {
        if (bidDto.getBidPrice() == null || bidDto.getBidPrice() <= 0) {
            throw new IllegalArgumentException("제안 가격은 0원보다 커야 합니다.");
        }
        if (bidDto.getBidPrice() % 100 != 0) {
            throw new IllegalArgumentException("제안 가격은 100원 단위로 입력해야 합니다.");
        }
        int result = bidMapper.updateBid(bidDto);
        if (result == 0) {
            throw new IllegalArgumentException("수정 권한이 없거나 이미 처리된 입찰입니다.");
        }
    }
    
}
