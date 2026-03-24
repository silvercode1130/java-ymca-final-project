package com.springbootstudy.bbs.controller;

import java.io.IOException;
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

import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.mapper.MemberMapper;
import com.springbootstudy.bbs.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberMapper memberMapper;
	
	@Autowired
    HttpServletRequest request;
    
    @Autowired
    //HttpSession session;


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
	// 세션, 서블릿 리퀘스트를 넣고 -> 서비스 들고 옴(model로)
	
	// login.jsp - 로그인 처리 기능
	@PostMapping("/views/member/login")
	public String login(Model model, HttpSession session, 
	                    @RequestParam("memId") String memId, 
	                    @RequestParam("memPwd") String memPwd,
	                    RedirectAttributes ra) { // 일회성 메세지 전달용(로그인 실패 시)

	    // 1. 로그인 체크 - 성공 시 1 / 실패 시 0
	    int result = memberService.loginMember(memId, memPwd);

	    if (result > 0) {
	    	// 로그인 성공 시
	        MemberVO memberVO = memberService.getMemberVO(memId);
	        
	        // 모델이 아니라 '세션'에 유저 객체를 담아야 유지됨
	        session.setAttribute("loginUser", memberVO);
	        session.setAttribute("isLogin", true);
	        
	        return "redirect:/boards"; 
	    } else {
	        // 로그인 실패 시
	        ra.addFlashAttribute("errorMsg", "아이디 혹은 비밀번호가 틀렸습니다");
	        return "redirect:/views/member/login"; 
	    }
	}

	// 지우지 말 것(에러나면 얘 써야 함!!!)
//	@PostMapping("/views/member/login")
//    public String login(Model model, HttpSession session, HttpServletResponse response,
//    		@RequestParam("memId") String memId, @RequestParam("memPwd") String memPwd) {
//		
//		// 로그인 성공 여부 체크
//		int result = memberService.loginMember(memId, memPwd);
//		
//		MemberVO memberVO = memberService.getMemberVO(memId);
//	
//		session.setAttribute("isLogin", true);
//		
//		model.addAttribute("memberVo", memberVO);
//		
//		return "redirect:/boards";
//	}
	
	
	// 비번찾기 -----------------------------------------------------------------
	
	// pwdFind.jsp - 창 띄우기
	@GetMapping("/views/member/pwdFind")
	public String pwdFind() {
		
		
		return "/views/member/pwdFind"; // ## 수정 - main 생기면 바꿔
	}
	
	
	
	
}
