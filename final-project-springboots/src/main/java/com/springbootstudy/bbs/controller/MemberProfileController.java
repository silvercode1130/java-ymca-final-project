package com.springbootstudy.bbs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.mapper.MemberProfileMapper;

import jakarta.servlet.http.HttpSession;

@Controller
public class MemberProfileController {

	@Autowired
	HttpSession session; 
	
	@Autowired
	MemberProfileMapper memberProfileMapper;

	
	// 회원정보 수정 -----------------------------------------------------------------

	// memberUpdate.html - 창 띄우기
	@GetMapping("/members/memberUpdate")
	public String memberUpdate(HttpSession session, Model model) {

		/*
		 * 로그인 한 뒤 -> 회원 정보를 받아서 페이지를 열어야 함
		 * 안그러면 500 에러
		 * 회원정보를 받을려면 로그인 할 때 값을 담은 session과 model이 필요함
		 */
		// 로그인 정보 가져오기
		MemberVO memberVO = (MemberVO) session.getAttribute("loginUser");

		// 로그인 안했으면 쫓아내기
		if (memberVO == null) {
			return "redirect:/members/login"; 
		}
		
		String gradeName = memberProfileMapper.selectGradeNameByMemId(memberVO.getMemId());
		
		model.addAttribute("memberVO", memberVO); 
		model.addAttribute("gradeName", gradeName);	

		return "views/member/memberUpdate"; 
	}
	
	// memberUpdate.html - 수정하기
	@PostMapping("/members/memberUpdate")
	public String memberUpdate(MemberVO vo,
	        @RequestParam(value="newPwd", required = false) String newPwd,
	        HttpSession session) {
		 
		// 세션에서 회원 조회
		MemberVO sessionUser = (MemberVO) session.getAttribute("loginUser");

	    // null이면 로그인부터
	    if (sessionUser == null) {
	    	return "redirect:/members/login"; 
	    }

	    // not null 값 값 넣기
	    vo.setMemId(sessionUser.getMemId());
	    vo.setMemIdx(sessionUser.getMemIdx());
	    vo.setMemGradeIdx(sessionUser.getMemGradeIdx());
	    vo.setMemIp(sessionUser.getMemIp());

	    // 비번 발급
	    if (newPwd != null && !newPwd.isEmpty()) {
	        vo.setMemPwd(newPwd);
	    } else {
	        vo.setMemPwd(sessionUser.getMemPwd());
	    }

	    memberProfileMapper.update(vo);	

	    // 수정된 정보로 저장
	    MemberVO updated = memberProfileMapper.selectOneFromId(vo.getMemId()); 
	    session.setAttribute("loginUser", updated);

	    return "redirect:/main"; 
	} 

}
