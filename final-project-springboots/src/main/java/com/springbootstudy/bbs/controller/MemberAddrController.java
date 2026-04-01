package com.springbootstudy.bbs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springbootstudy.bbs.domain.MemberAddrVO;
import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.service.MemberAddrService;
import com.springbootstudy.bbs.service.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MemberAddrController {

	@Autowired
	private MemberAddrService memberAddrService;
	
	// 주소등록창 -----------------------------------------------------------------
	
	@GetMapping("/members/memberAddrUpdate") 
	public String memberAddrUpdate(HttpSession session, Model model) {

	    MemberVO loginMember = (MemberVO) session.getAttribute("loginUser");

	    if (loginMember == null) {
	        return "redirect:/members/login";
	    }

	    model.addAttribute("member", loginMember); 

	    return "/views/member/memberAddrUpdate"; 
	}
	
	// memberAddrUpdate.html 의 Ajax 저장용
	@PostMapping("/member/updateAddrAjax.do")
	@ResponseBody
	public int updateAddr(MemberAddrVO vo) {

		// 두개 확인 필요
	    int result = memberAddrService.registerAddr(vo);
	    vo.setIsPrimary("N");
	    
	    return result;
	}

	// 주소목록창 -----------------------------------------------------------------
	
	// 저장된 값 보여주기
    @GetMapping("/members/memberAddr") 
    public String memberAddr(HttpSession session, Model model) {

    	// 로그인 안했으면 쫓아내기
        MemberVO loginMember = (MemberVO) session.getAttribute("loginUser");
        if (loginMember == null) {
            return "redirect:/members/login";
        }

        List<MemberAddrVO> addrList = memberAddrService.selectAddrList(loginMember.getMemIdx());
        model.addAttribute("addrList", addrList);
        model.addAttribute("member", loginMember);

        return "/views/member/memberAddr"; 
    } 



}
