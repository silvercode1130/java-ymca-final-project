package com.springbootstudy.bbs.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springbootstudy.bbs.domain.AuctionDTO;
import com.springbootstudy.bbs.domain.BidDTO;
import com.springbootstudy.bbs.domain.BoardVO;
import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.domain.MyPagePaymentVO;

import com.springbootstudy.bbs.mapper.DeliveryMapper;
import com.springbootstudy.bbs.mapper.PaymentMapper;
import com.springbootstudy.bbs.service.MyPageService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

  private final MyPageService mypageService;
  private final PaymentMapper paymentMapper;
  private final DeliveryMapper deliveryMapper;

  // 내 경매 목록
  @GetMapping("/auctions")
  public String myAuctions(HttpSession session, Model model) {

    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "redirect:/members/login";
    }

    Long memIdx = loginUser.getMemIdx();
    List<AuctionDTO> auctions = mypageService.getMyAuctions(memIdx);
    model.addAttribute("auctions", auctions);

    return "views/mypage/auctions";
  }

  // 🔥 핵심: 내 입찰 목록 (delivery 포함)
  @GetMapping("/bids")
  public String myBids(HttpSession session, Model model) {

    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "redirect:/members/login";
    }

    Long memIdx = loginUser.getMemIdx();
    List<BidDTO> bids = mypageService.getMyBids(memIdx);

    List<Map<String, Object>> bidViews = new ArrayList<>();

    for (BidDTO bid : bids) {
      Map<String, Object> row = new HashMap<>();
      row.put("bid", bid);
      row.put("delivery", deliveryMapper.findDeliveryByBidIdx(bid.getBidIdx()));
      row.put("payment", paymentMapper.findPaymentByBidIdx(bid.getBidIdx()));
      bidViews.add(row);
    }

    model.addAttribute("bids", bidViews);

    return "views/mypage/bids";
  }

  // 내 게시글 목록
  @GetMapping("/boards")
  public String myBoards(HttpSession session, Model model) {

    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "redirect:/members/login";
    }

    Long memIdx = loginUser.getMemIdx();
    List<BoardVO> boards = mypageService.getMyBoards(memIdx);
    model.addAttribute("boards", boards);

    return "views/mypage/boards";
  }

  // 구매자 결제 내역
  @GetMapping("/payments")
  public String myPayments(HttpSession session, Model model) {
    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
    if (loginUser == null)
      return "redirect:/members/login";

    List<MyPagePaymentVO> payments = mypageService.getMyPayments(loginUser.getMemIdx());
    model.addAttribute("payments", payments);

    return "views/mypage/payments";
  }

  // 판매자 판매 내역
  @GetMapping("/sales")
  public String mySales(HttpSession session, Model model) {
    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
    if (loginUser == null)
      return "redirect:/members/login";

    var sales = mypageService.getMySales(loginUser.getMemIdx());
    model.addAttribute("sales", sales);

    return "views/mypage/sales";
  }

  // 멤버 탈퇴
  @GetMapping("/delete")
  public String memberDelete(HttpSession session, Model model) {
    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
    if (loginUser == null)
      return "redirect:/members/login";

    return "views/mypage/memberDelete";
  }

}