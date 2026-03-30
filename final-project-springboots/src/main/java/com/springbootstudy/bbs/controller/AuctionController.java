package com.springbootstudy.bbs.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.springbootstudy.bbs.domain.AuctionListDTO;
import com.springbootstudy.bbs.domain.AuctionVO;
import com.springbootstudy.bbs.service.AuctionService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class AuctionController {
	
	@Autowired
	private AuctionService auctionService;
	
	@GetMapping({"/", "/auctionList"})
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
	    
	    // 상세보기 화면으로 이동
	    return "views/auction/auctionDetail"; 
	}
}
