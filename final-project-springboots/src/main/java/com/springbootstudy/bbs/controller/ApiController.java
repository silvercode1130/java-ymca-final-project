package com.springbootstudy.bbs.controller;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.service.ApiService; 

import jakarta.servlet.http.HttpSession; 

@Controller
public class ApiController {
	
	@Autowired
	ApiService apiService;
	// 네이버 API
	@GetMapping("/members/naverCallback")
	public String naverCallback() {

		return "views/member/naverCallback";
	}
	
	/*
	 * 네이버에서 정보 다 받아오기 (이름, 이메일, 번호, 생일 등등)
	 * 아이디는 네이버 계정으로 자동으로 감
	 * 비번은 랜덤으로 돌림
	 * -> 비번은 사용자가 알지도 못하고 찾지도 못함
	 * 컬럼 추가해서 로그인 타입 구분하고
	 * 네이버 회원이 일반로그인 시도하면 alert으로 성질 좀 내주고
	 * */
	
	// db 없이 로그인 시키기
	@RequestMapping("/members/naverCallback") 
	public String naverCallback(
	    @RequestParam("memName") String memName,
	    @RequestParam("memEmail") String memEmail,
	    @RequestParam("memTel") String memTel,
	    @RequestParam("memBday") String memBday,
	    HttpSession session
	) {

	    String memId = memEmail;
	    String tempPwd = "TMP_" + UUID.randomUUID().toString().substring(0, 8);

	    String memIp = "127.0.0.1";
	    Integer memRoleIdx = 1; // 일반 유저                 
	    Integer memGradeIdx = 1; // 기본 등급(최저)			

	    MemberVO user = apiService.findByEmail(memEmail);

	    if (user == null) {
	    	LocalDate bday = LocalDate.parse(memBday); 
	    	
	    	apiService.register(memId, tempPwd, memName, memTel, memEmail,
	                memIp, memRoleIdx, memGradeIdx, bday, "NAVER"); 

	        user = apiService.findByEmail(memEmail);  
	    }

	    session.setAttribute("loginUser", user);
	    
	    System.out.println("내가 그래도 컨트롤러는 접근 했어!!!" + memEmail);

	    return "redirect:/main";
	}
//	@PostMapping("/members/naverCallback")
//	public String naverCallback( 
//	    @RequestParam("memName") String memName,   // 네이버에서 받은 이름
//	    @RequestParam("memEmail") String memEmail,  // 네이버에서 받은 이메일
//	    @RequestParam("memTel") String memTel,    // (입력받거나 네이버에서 가져온 번호)
//	    @RequestParam("memBday") String memBday,    // (네이버에서 가져온 생일) 
//	    HttpSession session
//	) {
//	    
//	    // 네이버 이메일을 아이디로
//	    String memId = memEmail; 
//	    
//	    String tempPwd = "TMP_" + UUID.randomUUID().toString().substring(0, 8);
//	    
//	    // not null 값 채우기
//	    String memIp = "127.0.0.1";
//	    long memRoleIdx = 1;  // 일반유저 권한
//	    int memGradeIdx = 1;  // 일반 등급
//	    
//	    session.setAttribute("isTempPwd", true); 
//	    
//	    memberService.register(memId, tempPwd, memName, memTel, memEmail, memIp, memRoleIdx, memGradeIdx, memBday);
//
//	    session.setAttribute("loginUser", memName);
//	    session.setAttribute("isTemp", "Y"); 
//	    
//	    return "redirect:/main"; // 메인으로 이동!
//	} 
	
	
}
