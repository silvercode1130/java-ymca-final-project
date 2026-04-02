package com.springbootstudy.bbs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.springbootstudy.bbs.domain.MemberProfileVO;
import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.mapper.MemberProfileMapper;
import com.springbootstudy.bbs.service.MemberProfileService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MemberProfileController {

	@Autowired
	private MemberProfileService memberProfileService;
	
	// 회원 프로필 띄우기 ---------------------------------------------------------------
	
	// 프로필 조회 (페이지 이동)
    @GetMapping("members/memberProfileUpdate")
    public String memberProfile(HttpSession session, Model model) {
    	
    	MemberVO loginMember = (MemberVO) session.getAttribute("loginUser");

    	// 로그인 안했으면 로그인 창으로 
	    if (loginMember == null) {
	        return "redirect:/members/login";
	    }
	    
	    Long memIdx = ((MemberVO)session.getAttribute("loginUser")).getMemIdx();
	    
        MemberProfileVO profile = memberProfileService.getProfile(memIdx); 
        
        model.addAttribute("profile", profile);

        return "/views/member/memberProfileUpdate"; 
    }

    
    // 프로필 저장 (수정)
    @PostMapping("members/memberProfileUpdate")
    public String updateProfile(MemberProfileVO vo, @RequestParam("memImgFile") MultipartFile memImgFile) {
    	
    	if (!memImgFile.isEmpty()) {
            // 실제 파일 저장 로직 (나중에 파일 이름 추출해서 vo.setMemImg에 넣기)
            String fileName = memImgFile.getOriginalFilename();
            vo.setMemImg(fileName); // DB에는 파일 이름만 쏙!
            
            System.out.println("파일 이름: " + fileName);
            System.out.println("파일 크기: " + memImgFile.getSize());
        }
    	
        System.out.println("memIdx: " + vo.getMemIdx());
        System.out.println("닉네임: " + vo.getMemNickname());
        System.out.println("소개: " + vo.getMemIntro());
        System.out.println("파일: " + memImgFile.getOriginalFilename());  

        memberProfileService.updateProfile(vo);

        return "redirect:/members/memberProfileUpdate?memIdx=" + vo.getMemIdx();  
    }

	
	

}
