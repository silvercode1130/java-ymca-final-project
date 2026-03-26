package com.springbootstudy.bbs.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.springbootstudy.bbs.domain.AuctionVO;
import com.springbootstudy.bbs.domain.BidVO;
import com.springbootstudy.bbs.service.MyPageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MyPageController {
  private final MyPageService myPageService;

  // 내 경매 목록
  @GetMapping("auctions/{memIdx}")
  public String getAuctionList(@PathVariable("memIdx") int memIdx, Model model) {
    List<AuctionVO> auctionList = myPageService.getAuctionList(memIdx);

    model.addAttribute("auctions", auctionList);

    return "views/mystatus/auctionview";
  }

  // 내 입찰 내역
  @GetMapping("bids/{memIdx}")
  public String getBidList(@PathVariable("memIdx") int memIdx, Model model) {
    List<BidVO> bidList = myPageService.getBidList(memIdx);

    model.addAttribute("bids", bidList);

    return "views/mystatus/bidview";
  }
}
