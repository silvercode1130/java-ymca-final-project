package com.springbootstudy.bbs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springbootstudy.bbs.domain.AuctionListDTO;
import com.springbootstudy.bbs.domain.BidListDTO;
import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.service.AuctionService;
import com.springbootstudy.bbs.service.BidService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class AuctionController {
	
	@Autowired
	private AuctionService auctionService;
	
	@Autowired
	private BidService bidService;
	
	// 구매 요청 전체 리스트 조회
	@GetMapping({"/auctionList"})
    public String auctionList(Model model) { // 쟁반(Model)을 준비
        
        // 서비스한테서 리스트를 가져옴
        List<AuctionListDTO> list = auctionService.AuctionList();
        
        // 쟁반에 리스트를 올리고 'auctionList'라는 이름표를 붙임
        model.addAttribute("auctionList", list);
        
        // "views/auction/auctionList.html" 화면으로 쟁반을 들고 이동
        return "views/auction/auctionList"; 
    }
	
	// 구매 요청 글 상세페이지(입찰 제안 등 포함)
	@GetMapping("/auction/auctionDetail/{auctionIdx}")
	public String auctionDetail(@PathVariable("auctionIdx") Long auctionIdx, Model model) {
	    
	    // 서비스 호출
	    AuctionListDTO detail = auctionService.auctionDetail(auctionIdx);
	    
	    // 만약 없는 글 번호라면? (예외 처리)
	    if (detail == null) {
	        return "redirect:/auction/list"; // 다시 리스트로
	    }
	    
	    // 모델에 데이터 담기
	    model.addAttribute("detail", detail);
	    
	    
	    // ======================================================================
	    
	    // bid 관련
	    
        // 해당 글의 입찰 리스트 가져오기
        List<BidListDTO> bidList = bidService.BidList(auctionIdx);
        
        model.addAttribute("detail", detail);
        model.addAttribute("bidList", bidList); // HTML로 던지기
	    
	    // 상세보기 화면으로 이동
	    return "views/auction/auctionDetail"; 
	}
	
	// 등록 폼으로 이동 (GET)
    @GetMapping("/auction/register")
    public String registerForm(HttpSession session) {
        // 세션 이름 'loginUser'로 통일! 
        if (session.getAttribute("loginUser") == null) {
            log.info("로그인 정보 없음 -> 로그인 페이지로 리다이렉트");
            return "redirect:/login"; 
        }
        return "views/auction/auctionRegister";
    }

    // 등록 실행 (POST)
    @PostMapping("/auction/register")
    public String registerAction(AuctionListDTO dto, HttpSession session,
                                  RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        dto.setBuyerIdx(loginUser.getMemIdx());
        log.info("경매 등록 시도: {}", dto);

        try {
            auctionService.registerAuction(dto);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auction/register?error";
        } catch (Exception e) {
            log.error("경매 등록 중 에러 발생", e);
            ra.addFlashAttribute("errorMessage", "등록 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/auction/register?error";
        }

        return "redirect:/auctionList";
    }
	
    // 입찰 등록
    @PostMapping("/auction/bid")
    public String registerBid(BidListDTO bidDto, HttpSession session) {
    	MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        bidDto.setBidderIdx(loginUser.getMemIdx());

        // 현재 입찰하려는 경매글의 아이템 번호라도 넣어줘야 DB 에러가 안 남
        // (지금 당장 상세페이지에서 itemIdx를 안 보내주고 있다면 여기서 강제로 세팅)
        if (bidDto.getItemIdx() == null) {
            // 실제로는 해당 auctionIdx로 조회해서 가져와야 하지만, 
            // 일단 에러 방지를 위해 1번이라도 넣어보고 테스트
            bidDto.setItemIdx(1L); 
        }

        bidService.registerBid(bidDto);
        return "redirect:/auction/auctionDetail/" + bidDto.getAuctionIdx();
    }
    
}
