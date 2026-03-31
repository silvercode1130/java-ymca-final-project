package com.springbootstudy.bbs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.service.MyPageService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MyPageController {
  private final MyPageService myPageService;

  private MemberVO getSessionUser(HttpSession session) {
    return (MemberVO) session.getAttribute("loginUser");
  }

  @GetMapping("/mypage/boards")
  public String getMyboards(HttpSession session, Model model) {
    MemberVO member = getSessionUser(session);
    if (member == null)
      return "redirect:/members/login";

    model.addAttribute("boards", myPageService.getBoardList(member.getMemIdx()));
    return "views/mystatus/myboardview";
  }

  @GetMapping("/mypage/auctions")
  public String getAuctionList(HttpSession session, Model model) {
    MemberVO member = getSessionUser(session);
    if (member == null)
      return "redirect:/members/login";

    model.addAttribute("auctions", myPageService.getAuctionList(member.getMemIdx()));
    return "views/mystatus/auctionview";
  }

  @GetMapping("/mypage/bids")
  public String getBidList(HttpSession session, Model model) {
    MemberVO member = getSessionUser(session);
    if (member == null)
      return "redirect:/members/login";

    model.addAttribute("bids", myPageService.getBidList(member.getMemIdx()));
    return "views/mystatus/bidview";
  }
}
