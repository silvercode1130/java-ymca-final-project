package com.springbootstudy.bbs.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	@GetMapping("/views/member/signUp.do")
	public String signUp() {
		
		
		return "/views/member/signUp"; 
	}
	
	
	// signUp.jsp - 회원가입 처리 기능
	// signUp.jsp - 회원가입 처리 기능
	@PostMapping("/views/member/signUp.do")
	public String signUp(
			@RequestParam("memId") String memId,
	        @RequestParam("memPwd") String memPwd,
	        @RequestParam("memName") String memName,
	        @RequestParam("memTel") String memTel,
	        @RequestParam("memEmail") String memEmail,
	        HttpServletRequest request 
	) { 
	    // 1. DB에 넣기 전에 미리 필수 값들을 준비하자!
	    String memIp = request.getRemoteAddr();
	    if (memIp.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {	// memIp 자체를 못찾음
	    	memIp = "127.0.0.1";	// memIp cannot be resolved to a variable
	    		 
	    }
	    
	    long memRoleIdx = 1L;  // 기본 권한 1번
	    int memGradeIdx = 1;   // 기본 등급 1번 
 
	    // 2. 서비스로 보낼 때 이 값들을 다 같이 던져줘야 해! 
	    // 65번 에러
	    int result = memberService.insertMember(memId, memPwd, memName, memTel, memEmail, memIp);
	    // The method insertMember(String, String, String, String, String) in the type MemberService is not 
	    //		applicable for the arguments (String, String, String, String, String, String, long, int)
	    
	    if (result > 0) {
	        return "redirect:/views/member/login.do"; 
	    } else {
	        return "redirect:/views/member/signUp.do";
	    }
	}
//	@PostMapping("/views/member/signUp.do")
//    public String signUp(
//            @RequestParam("memId") String memId,
//            @RequestParam("memPwd") String memPwd,
//            @RequestParam("memName") String memName,
//            @RequestParam("memTel") String memTel,
//            @RequestParam("memEmail") String memEmail,
//            Model model,
//            HttpServletRequest request
//    ) { 
//        int result = memberService.insertMember(memId, memPwd, memName, memTel, memEmail);
//        
//        // 1. 사용자의 진짜 IP 주소 가져오기
//        String userIp = request.getRemoteAddr();
//        if (userIp.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
//            userIp = "127.0.0.1"; // 로컬 테스트용 처리
//        }
//
//        // 2. 모델에 값 담기 (이 이름으로 HTML에서 부를 거야!)
//        model.addAttribute("memIp", userIp);
//        model.addAttribute("memRoleIdx", 1);  // 기본 권한 1번
//        model.addAttribute("memGradeIdx", 1); // 기본 등급 1번
// 
//        if (result > 0) {
//            return "redirect:/views/member/login";
//        } else {
//            return "redirect:/views/member/signUp";
//        }
//    }

	
	
	// signUp.jsp - 중복 아이디 검사
	@ResponseBody
	@GetMapping("/views/member/check_id.do")
	public Map<String, Boolean> check_id(@RequestParam("memId")String memId) {

		int count = memberMapper.countByMemId(memId);	// Cannot make a static reference to the non-static method countByMemId(String) from the type MemberMapper
		
		boolean isDuplicate = count > 0;

		Map<String, Boolean> map = new HashMap<>();
		map.put("result", isDuplicate); 
	

		return map;
	} 
	
	
	// 로그인 -----------------------------------------------------------------
	
	// login.jsp - 창 띄우기
	@GetMapping("/views/member/login.do")
	public String login() {
		
		
		return "/views/member/login"; 
	}
	
	
	// login.jsp - 로그인 처리 기능
	@PostMapping("/views/member/login.do")
    public String login(
            @RequestParam("memId") String memId,
            @RequestParam("memPwd") String memPwd
    ) {
        int result = memberService.loginMember(memId, memPwd); 
        // Cannot make a static reference to the non-static method loginMember(String, String) from the type MemberService
 
        if (result > 0) {
            return "redirect:/views/member/signUp.do";
        } else {
            return "redirect:/views/member/login.do";
        }
    }
	
	
	// 비번찾기 -----------------------------------------------------------------
	
	// pwdFind.jsp - 창 띄우기
	@GetMapping("/views/member/pwdFind.do")
	public String pwdFind() {
		
		
		return "/views/member/pwdFind"; // ## 수정 - main 생기면 바꿔
	}
	
	
	
	
}
