package com.springbootstudy.bbs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springbootstudy.bbs.domain.BidDTO;
import com.springbootstudy.bbs.domain.MemberAddrVO;
import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.service.BidService;
import com.springbootstudy.bbs.service.MemberAddrService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentPageController {

  private final BidService bidService;
  private final MemberAddrService memberAddrService;

  // 1. 결제 페이지 (bid 정보 + 대표 배송지 함께 전달)
  @GetMapping("/pay")
  public String payPage(@RequestParam("bidIdx") Long bidIdx,
      HttpSession session, Model model) {

    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
    if (loginUser == null)
      return "redirect:/members/login";

    // 입찰 정보 조회
    BidDTO bid = bidService.findBidById(bidIdx);
    if (bid == null)
      return "redirect:/auctions";

    // 대표 배송지 조회
    MemberAddrVO addr = memberAddrService.getPrimaryAddr(loginUser.getMemIdx());

    model.addAttribute("bid", bid);
    model.addAttribute("addr", addr);
    model.addAttribute("memTel", loginUser.getMemTel()); // 세션에서 직접 꺼내기

    return "views/payment/pay";
  }

  // 2. 결제 성공 페이지
  @GetMapping("/success")
  public String successPage() {
    return "views/payment/pay_success";
  }

  // 3. 결제 실패 페이지
  @GetMapping("/fail")
  public String failPage() {
    return "views/payment/pay_fail";
  }
}