package com.springbootstudy.bbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springbootstudy.bbs.domain.AuctionDTO;
import com.springbootstudy.bbs.domain.BidDTO;
import com.springbootstudy.bbs.mapper.AuctionMapper;
import com.springbootstudy.bbs.mapper.BidMapper;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class BidService {
	
	@Autowired
	private BidMapper bidMapper;
	
	@Autowired
	private AuctionMapper auctionMapper;

    @Autowired
    private NotificationService notificationService;
	
	// 특정 경매의 입찰 리스트 조회 (이름 마스킹 포함)
	public List<BidDTO> BidList(Long auctionIdx) {
	    List<BidDTO> list = bidMapper.BidList(auctionIdx);

	    for (BidDTO dto : list) {
	        // memName(실명)을 마스킹해서 bidderName에 세팅
	        String name = dto.getMemName();
	        if (name != null && name.length() > 1) {
	            dto.setBidderName(name.substring(0, 1) + "*".repeat(name.length() - 1));
	        } else {
	            dto.setBidderName(name); // 한 글자 이름은 그대로
	        }
	        
	        // memId(아이디)를 마스킹해서 bidderMemIdMasked에 세팅
	        String memId = dto.getBidderMemId();
	        if (memId != null && memId.length() > 2) {
	            // 첫 2글자 + * + 마지막 1글자 형태로 마스킹 (예: user123 → us***3)
	            int len = memId.length();
	            dto.setBidderMemIdMasked(memId.substring(0, 2) + "*".repeat(Math.max(1, len - 3)) + memId.substring(len - 1));
	        } else if (memId != null) {
	            // 3글자 미만은 간단하게 처리
	            dto.setBidderMemIdMasked(memId.substring(0, 1) + "*".repeat(Math.max(1, memId.length() - 1)));
	        }
	    }
	    return list;
	}
    
    // 입찰 등록 (아이템 정보 선행 등록 포함)
    public void registerBid(BidDTO bidDto) {

        // 입찰가 검증 - 음수/0 방지
        if (bidDto.getBidPrice() == null || bidDto.getBidPrice() <= 0) {
            throw new IllegalArgumentException("제안 가격은 0원보다 커야 합니다.");
        }
        // 1000원 단위 검증
        if (bidDto.getBidPrice() % 1000 != 0) {
            throw new IllegalArgumentException("제안 가격은 1000원 단위로 입력해야 합니다.");
        }
        
        // 역경매 특성상 제안하는 아이템 정보부터 insert (itemIdx 추출)
        if(bidDto.getItemName() == null) bidDto.setItemName("입찰 제안 상품"); 
        bidMapper.insertItem(bidDto);
        
        // 위에서 생성된 itemIdx를 가지고 입찰(bid) 정보 저장
        bidMapper.insertBid(bidDto);

    }
    
    // 입찰 삭제 - 소프트 딜리트
    public void deleteBid(Long bidIdx, Long bidderIdx) {
        int result = bidMapper.softDeleteBid(bidIdx, bidderIdx);
        if (result == 0) {
            throw new IllegalArgumentException("삭제 권한이 없거나 존재하지 않는 입찰입니다.");
        }
    }
    
    // 관리자 입찰 삭제
    @Transactional
    public void adminDeleteBid(Long bidIdx) {
        int result = bidMapper.adminDeleteBid(bidIdx);
        if (result == 0) {
            throw new IllegalArgumentException("존재하지 않는 입찰입니다.");
        }
    }
    
    // 낙찰 처리 (경매 상태 변경 및 타 입찰 거절 포함)
    @Transactional
    public void selectWinner(Long bidIdx, Long auctionIdx) {
		BidDTO winnerBid = bidMapper.findBidById(bidIdx);

    	// 해당 입찰건을 '낙찰(2)' 상태로 변경
        int result = bidMapper.selectWinnerBid(bidIdx, auctionIdx);
        if (result == 0) {
            throw new IllegalArgumentException("낙찰 처리에 실패했습니다.");
        }
        // 해당 경매의 나머지 모든 입찰을 '실패(3)' 처리
        bidMapper.rejectOtherBids(auctionIdx, bidIdx);
        
        // 경매 자체의 상태를 '마감(3)'으로 즉시 변경
        auctionMapper.updateAuctionStatus(auctionIdx, 3);

		notificationService.notifyWinnerSelectedToBidder(auctionIdx, winnerBid);
    }

    // 입찰 단건 상세 조회
    public BidDTO findBidById(Long bidIdx) {
        BidDTO dto = bidMapper.findBidById(bidIdx);
        if (dto != null) {
            String name = dto.getMemName();
            if (name != null && name.length() > 1) {
                dto.setBidderName(name.substring(0, 1) + "*".repeat(name.length() - 1));
            } else {
                dto.setBidderName(name);
            }
            
            // memId 마스킹 처리
            String memId = dto.getBidderMemId();
            if (memId != null && memId.length() > 2) {
                int len = memId.length();
                dto.setBidderMemIdMasked(memId.substring(0, 2) + "*".repeat(Math.max(1, len - 3)) + memId.substring(len - 1));
            } else if (memId != null) {
                dto.setBidderMemIdMasked(memId.substring(0, 1) + "*".repeat(Math.max(1, memId.length() - 1)));
            }
        }
        return dto;
    }

}
