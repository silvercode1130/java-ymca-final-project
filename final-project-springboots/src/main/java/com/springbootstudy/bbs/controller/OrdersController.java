package com.springbootstudy.bbs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.domain.OrdersVO;
import com.springbootstudy.bbs.service.OrdersService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/mypage")
public class OrdersController {
	
	  @Autowired
	  private OrdersService ordersService;
	
	  // 내 거래 목록
	  @GetMapping("/orders")
	  public String myOrders(HttpSession session, Model model) {

	    MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
	    if (loginUser == null) {
	      return "redirect:/members/login";
	    }

	    Long memIdx = loginUser.getMemIdx();
	    List<OrdersVO> orders = ordersService.getMyOrders(memIdx);
	    model.addAttribute("orders", orders);

	    return "views/mypage/orders";
	  }
}
