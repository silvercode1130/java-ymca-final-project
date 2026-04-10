package com.springbootstudy.bbs.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springbootstudy.bbs.service.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ApiController {
	
	@Autowired
	MemberService memberService;

	// 네이버 API
	@GetMapping("/members/naverCallback")
	public String naverCallback() {


		//return "views/member/naverCallback";
		return "views/main/main";
	}
	
	/*
	 * 네이버에서 정보 다 받아오기 (이름, 이메일, 번호, 생일 등등)
	 * 로그인 시 임시 비밀번호 발급
	 * 회원정보로 리다이렉트 시켜서 임시 비번을 바꾸게 alert 띄우기
	 * 
	 * <그 외 선택지>
	 * 1. api 없애기
	 * 2. 지금 방식대로 하기
	 * 3. member에 컬럼 하나 추가하기
	 * */
	
	// db 없이 로그인 시키기
	@PostMapping("/members/naverCallback")
	public String naverCallback( 
	    @RequestParam("memName") String memName,   // 네이버에서 받은 이름
	    @RequestParam("memEmail") String memEmail,  // 네이버에서 받은 이메일
	    @RequestParam("memTel") String memTel,    // (입력받거나 네이버에서 가져온 번호)
	    @RequestParam("memBday") String memBday,    // (네이버에서 가져온 생일) 
	    HttpSession session
	) {
	    
	    // 네이버 이메일을 아이디로
	    String memId = memEmail; 
	    
	    String tempPwd = "TMP_" + UUID.randomUUID().toString().substring(0, 8);
	    
	    // not null 값 채우기
	    String memIp = "127.0.0.1";
	    long memRoleIdx = 1;  // 일반유저 권한
	    int memGradeIdx = 1;  // 일반 등급
	    
	    session.setAttribute("isTempPwd", true); 
	    
	    memberService.register(memId, tempPwd, memName, memTel, memEmail, memIp, memRoleIdx, memGradeIdx, memBday);

	    session.setAttribute("loginUser", memName);
	    session.setAttribute("isTemp", "Y"); 
	    
	    return "redirect:/main"; // 메인으로 이동!
	}
	
	
}
