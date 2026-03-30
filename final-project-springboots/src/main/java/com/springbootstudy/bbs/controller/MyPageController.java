package com.springbootstudy.bbs.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.springbootstudy.bbs.domain.AuctionVO;
import com.springbootstudy.bbs.domain.BidVO;
import com.springbootstudy.bbs.domain.BoardVO;
import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.service.MyPageService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MyPageController {
  private final MyPageService myPageService;

  // 세션에서 유저 정보를 가져오고 형변환까지 처리
  private MemberVO getSessionUser(HttpSession session) {
    return (MemberVO) session.getAttribute("loginUser"); // 세션 키 이름 확인!
  }

  // 1. 내 게시글 목록
  @GetMapping("board")
  public String getMyboards(HttpSession session, Model model) {
    MemberVO member = getSessionUser(session);
    if (member == null)
      return "redirect:/login"; // 로그인 체크

    model.addAttribute("boards", myPageService.getBoardList(member.getMemIdx()));
    return "views/mystatus/myboardview";
  }

  // 2. 내 경매 목록
  @GetMapping("auctions")
  public String getAuctionList(HttpSession session, Model model) {
    MemberVO member = getSessionUser(session);
    if (member == null)
      return "redirect:/login";

    model.addAttribute("auctions", myPageService.getAuctionList(member.getMemIdx()));
    return "views/mystatus/auctionview";
  }

  // 3. 내 입찰 내역
  @GetMapping("bids")
  public String getBidList(HttpSession session, Model model) {
    MemberVO member = getSessionUser(session);
    if (member == null)
      return "redirect:/login";

    model.addAttribute("bids", myPageService.getBidList(member.getMemIdx()));
    return "views/mystatus/bidview";
  }
}