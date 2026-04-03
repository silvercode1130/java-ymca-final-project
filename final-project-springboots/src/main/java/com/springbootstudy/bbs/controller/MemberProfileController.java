package com.springbootstudy.bbs.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

	    // 로그인 안하면 로그인 부터
	    if (loginMember == null) {
	        return "redirect:/members/login";
	    }

	    Long memIdx = loginMember.getMemIdx();
	    MemberProfileVO profile = memberProfileService.getProfile(memIdx);

	    // null 방지용
	    if (profile == null) {
	        profile = new MemberProfileVO();
	    }

	    model.addAttribute("profile", profile);

	    return "/views/member/memberProfileUpdate";
	}

    
    // 프로필 저장 (수정)
    @PostMapping("members/memberProfileUpdate")
    public String updateProfile(MemberProfileVO vo,
                                @RequestParam("memImgFile") MultipartFile memImgFile) throws Exception {

    	if (!memImgFile.isEmpty()) {

    	    String uploadDir = "C:/upload/finalProfile/";

    	    File dir = new File(uploadDir);
    	    if (!dir.exists()) {
    	        dir.mkdirs();
    	    }

    	    String fileName = memImgFile.getOriginalFilename();
    	    File dest = new File(uploadDir + fileName);
    	    memImgFile.transferTo(dest);

    	    vo.setMemImg(fileName);

    	} else {
    	    // 수정 시 이미지 안바꿔도 이미지 안날라가게!
    	    MemberProfileVO exist = memberProfileService.getProfile(vo.getMemIdx());
    	    if (exist != null) {
    	        vo.setMemImg(exist.getMemImg());
    	    }
    	}

        System.out.println("파일 크기: " + memImgFile.getSize());
        System.out.println("memIdx: " + vo.getMemIdx());
        System.out.println("닉네임: " + vo.getMemNickname());
        System.out.println("소개: " + vo.getMemIntro());
        System.out.println("파일: " + memImgFile.getOriginalFilename());
        System.out.println("isEmpty: " + memImgFile.isEmpty());

        memberProfileService.updateProfile(vo);

        return "redirect:/members/memberProfileUpdate?memIdx=" + vo.getMemIdx();
    }
    
    
    // 닉네임 중복 체크 기능
    @PostMapping("/members/checkNickname")
    @ResponseBody
    public int checkNickname(@RequestParam("memNickname") String memNickname) {
        return memberProfileService.checkNickname(memNickname);
    }

	
	

}
