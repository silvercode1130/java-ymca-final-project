package com.springbootstudy.bbs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
	private BidService bidService; // BidService 주입!
	
	@GetMapping({"/auctionList"})
    public String auctionList(Model model) { // 쟁반(Model)을 준비
        
        // 서비스한테서 리스트를 가져옴
        List<AuctionListDTO> list = auctionService.AuctionList();
        
        // 쟁반에 리스트를 올리고 'auctionList'라는 이름표를 붙임
        model.addAttribute("auctionList", list);
        
        // "views/auction/auctionList.html" 화면으로 쟁반을 들고 이동
        return "views/auction/auctionList"; 
    }
	
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
	
	// 1. 등록 폼으로 이동 (GET)
    @GetMapping("/auction/register")
    public String registerForm(HttpSession session) {
        // 세션 이름 'loginUser'로 통일! ㅡㅡ^
        if (session.getAttribute("loginUser") == null) {
            log.info("로그인 정보 없음 -> 로그인 페이지로 리다이렉트");
            return "redirect:/login"; 
        }
        return "views/auction/auctionRegister";
    }

    // 2. 등록 실행 (POST)
    @PostMapping("/auction/register")
    public String registerAction(AuctionListDTO dto, HttpSession session) {
        // 세션에서 로그인 유저 객체 꺼내기
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        
        if (loginUser == null) {
            return "redirect:/login";
        }

        // DTO에 구매자 PK(memIdx) 심어주기
        dto.setBuyerIdx(loginUser.getMemIdx());
        
        log.info("경매 등록 시도: {}", dto);

        try {
            auctionService.registerAuction(dto);
        } catch (Exception e) {
            log.error("경매 등록 중 에러 발생!", e);
            return "redirect:/auction/register?error"; // 에러 시 다시 폼으로
        }

        // 성공 시 리스트 주소로 이동 (주소 확인 요망!)
        return "redirect:/auctionList"; 
    }
	
    // 입찰 등록
    @PostMapping("/auction/bid")
    public String registerBid(BidListDTO bidDto, HttpSession session) {
        // 세션에서 로그인한 유저(판매자) 정보 가져오기
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        
        // 데이터 세팅
        bidDto.setBidderIdx(loginUser.getMemIdx());

        // DB에 넣기
        bidService.registerBid(bidDto);

        // 다시 보던 상세페이지로 튕겨주기
        return "redirect:/auction/auctionDetail/" + bidDto.getAuctionIdx();
    }
    
}
