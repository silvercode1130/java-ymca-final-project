package com.springbootstudy.bbs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.springbootstudy.bbs.domain.MemberVO;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

   @GetMapping({"/", "main"})
   public String main(Model model, HttpSession session) {
      
      // 로그인 정보가 있으면 model에 추가해서 template에서 사용 가능하게 함
      MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
             
      if (loginUser != null) {
          model.addAttribute("loginUser", loginUser); 
       }

      return "fragments/main";   
      // 리다이렉트하면 에러 떠요
      // HomeController는 /, /main을 쓰는데 return "redirect:main"; 을 반환해서 /main -> /main -> /main 무한 반복
      // 에러 내용
      // 페이지가 작동하지 않습니다.
      // localhost에서 리디렉션한 횟수가 너무 많습니다.
      // 쿠키 삭제해 보기.
   } 
}
