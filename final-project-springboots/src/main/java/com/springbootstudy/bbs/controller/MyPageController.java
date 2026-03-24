package com.springbootstudy.bbs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springbootstudy.bbs.domain.AuctionVO;
import com.springbootstudy.bbs.domain.BidVO;
import com.springbootstudy.bbs.service.MyPageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/pageview")
@RequiredArgsConstructor
public class MyPageController {
  private final MyPageService myPageService;

  // 경매현황 조회
  @GetMapping("/auction/{auctionIdx}")
  public AuctionVO getAuction(@PathVariable int auctionIdx) {
    return myPageService.getAuction(auctionIdx);
  }

  // 입찰 현황 조회
  @GetMapping("/bid/{bidIdx}")
  public BidVO getBid(@PathVariable int bidIdx) {
    return myPageService.getBid(bidIdx);
  }
}
