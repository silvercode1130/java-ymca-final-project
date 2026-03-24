package com.springbootstudy.bbs.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springbootstudy.bbs.mapper.MemberMapper;
import com.springbootstudy.bbs.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberMapper memberMapper;

	// 회원가입 -----------------------------------------------------------------
	
	// signUp.jsp - 창 띄우기
	@GetMapping("/views/member/signUp")
	public String signUp() {
		
		
		return "views/member/signUp"; 
	}
	
	
	// signUp.jsp - 회원가입 처리 기능
	// 회원가입 처리
    @PostMapping("/views/member/signUp")
    public String signUp(
    		@RequestParam("memId") String memId,
            @RequestParam("memPwd") String memPwd,
            @RequestParam("memName") String memName,
            @RequestParam("memTel") String memTel,
            @RequestParam("memEmail") String memEmail,
            @RequestParam("memIp") String memIp,
            @RequestParam("memRoleIdx") Long memRoleIdx,
            @RequestParam("memGradeIdx") int memGradeIdx,
            Model model
    ) {

        try {
            memberService.insertMember(
                    memId, memPwd, memName, memTel, memEmail,
                    memIp, memRoleIdx, memGradeIdx
            );

            return "redirect:/views/member/login";

        } catch (Exception e) {
            e.printStackTrace();

            model.addAttribute("errorMessage", "회원가입 실패");
            return "views/member/signUp";
        }
    }

	
	// signUp.jsp - 중복 아이디 검사
	@ResponseBody
	@GetMapping("/views/member/check_id")
	public String check_id(@RequestParam("memId") String memId) {

	    int count = memberMapper.countByMemId(memId);
	    boolean isDuplicate = count > 0;

	    if (isDuplicate) {
	        return "duplicate";
	    } else {
	        return "ok";
	    }
	}
	
	
	// 로그인 -----------------------------------------------------------------
	
	// login.jsp - 창 띄우기
	@GetMapping("/views/member/login")
	public String login() {
		
		
		return "/views/member/login"; 
	}
	
	
	// login.jsp - 로그인 처리 기능
	@PostMapping("/views/member/login")
    public String login(
            @RequestParam("memId") String memId,
            @RequestParam("memPwd") String memPwd
    ) {
        int result = memberService.loginMember(memId, memPwd); 
        // Cannot make a static reference to the non-static method loginMember(String, String) from the type MemberService
 
        if (result > 0) {
            return "redirect:/boards";	// ## 수정 - 메인 생기면 메인으로 변경
        } else {
            return "redirect:/views/member/login";  
        }
    }
	
	
	// 비번찾기 -----------------------------------------------------------------
	
	// pwdFind.jsp - 창 띄우기
	@GetMapping("/views/member/pwdFind")
	public String pwdFind() {
		
		
		return "/views/member/pwdFind"; // ## 수정 - main 생기면 바꿔
	}
	
	
	
	
}
