package com.springbootstudy.bbs.controller;

import com.springbootstudy.bbs.domain.AuctionDTO;
import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.service.AuctionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private AuctionService auctionService;

    // 메인 페이지 ("/" 또는 "/main")
    @GetMapping({"/", "main"})
    public String main(Model model, HttpSession session) {

        // 로그인 정보 세팅
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser != null) {
            model.addAttribute("loginUser", loginUser);
        }

        // 메인에서 보여줄 경매 목록 (예: 전체 or 최신 N개)
        auctionService.updateExpiredAuctions();   // 상태 자동 업데이트
        List<AuctionDTO> auctionList = auctionService.AuctionList(null, null, null, null);

        model.addAttribute("auctionList", auctionList);
        model.addAttribute("keyword", null);
        model.addAttribute("selectedCategory", null);

        return "views/main/main"; 
    } 
}