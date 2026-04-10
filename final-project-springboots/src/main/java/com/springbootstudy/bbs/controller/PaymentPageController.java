package com.springbootstudy.bbs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payment")
public class PaymentPageController {

  // 1. 테스트 페이지 보여주기
  @GetMapping("/test")
  public String testPage() {
    return "/views/payment/pay_test";
  }

  // 2. 결제 성공 페이지 보여주기
  @GetMapping("/success")
  public String successPage() {
    return "/views/payment/pay_success";
  }

  // 3. 결제 실패 페이지 보여주기
  @GetMapping("/fail")
  public String failPage() {
    return "/views/payment/pay_fail";
  }
}