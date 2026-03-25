package com.springbootstudy.bbs.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
