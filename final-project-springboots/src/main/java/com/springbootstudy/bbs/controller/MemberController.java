package com.springbootstudy.bbs.controller;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
	HttpSession session;


	// 회원가입 -----------------------------------------------------------------

	// signUp.html - 창 띄우기
	@GetMapping("/members/signUp")
	public String signUp() {

		return "/views/member/signUp"; 
	}

	// signUp.html - 회원가입 처리 기능
	// 회원가입 처리
	@PostMapping("/members/signUp")
	public String signUp(
			@RequestParam("memId") String memId,
			@RequestParam("memPwd") String memPwd,
			@RequestParam("memName") String memName,
			@RequestParam("memTel") String memTel,
			@RequestParam("memEmail") String memEmail,
			@RequestParam("emailDomain") String emailDomain,
			@RequestParam("memIp") String memIp,
			@RequestParam("memRoleIdx") Long memRoleIdx,
			@RequestParam("memGradeIdx") int memGradeIdx,
			Model model) {
		
		String fullEmail = memEmail + emailDomain;

		try {
			memberService.insertMember(
					memId, memPwd, memName, memTel, fullEmail,
					memIp, memRoleIdx, memGradeIdx);

			return "redirect:/main";

		} catch (Exception e) {
			e.printStackTrace();

			model.addAttribute("errorMessage", "회원가입 실패하였습니다");
			return "views/member/signUp";  
		}
	}

	// signUp.html - 중복 아이디 검사
	@ResponseBody
	@GetMapping("/members/check_id")
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

	// login.html - 창 띄우기
	@GetMapping("/members/login")
	public String loginForm(HttpSession session, Model model) {

	    String msg = (String) session.getAttribute("loginMsg");

	    if (msg != null) {
	        model.addAttribute("loginMsg", msg);
	        session.removeAttribute("loginMsg"); 
	    }

	    return "views/member/login";
	}
	
	// 세션, 서블릿 리퀘스트를 넣고 -> 서비스 들고 옴(model로)
	// login.html - 로그인 처리 기능
	@PostMapping("/members/login")
	public String login(@RequestParam("memId") String memId,
	                    @RequestParam("memPwd") String memPwd,
	                    Model model,
	                    HttpSession session,
	                    RedirectAttributes ra) throws ServletException, IOException {

	    // 로그인 성공 여부 확인
	    int result = memberService.login(memId, memPwd);

	    if (result == -1) { // 아이디 없음
	        ra.addFlashAttribute("error", "존재하지 않는 아이디입니다.");
	        return "redirect:/members/login";

	    } else if (result == 0) { // 비밀번호 틀림
	        ra.addFlashAttribute("error", "비밀번호가 틀립니다.");
	        return "redirect:/members/login";
	    }

	    // 로그인 성공 → 회원 정보 세션 저장
	    MemberVO memberVO = memberService.getMemberVO(memId);

	    session.setAttribute("isLogin", true);
	    session.setAttribute("loginId", memId);
	    session.setAttribute("loginUser", memberVO);

	    System.out.println("memberVO.name : " + memberVO.getMemName());

	    return "redirect:/main";
	}

	// 비번찾기 -----------------------------------------------------------------
	// 회원정보 수정 / 삭제 기능이 구현 된 이후 구현할 예정!!

	// pwdFind.html - 창 띄우기
	@GetMapping("/members/pwdFind")
	public String pwdFind() {

		return "/views/member/pwdFind"; // ## 수정 - main 생기면 바꿔
	}
	

	// 로그아웃 -----------------------------------------------------------------

	@GetMapping("/memberLogout")
	public String logout(HttpSession session) {	
		
		// 세션 지움
		session.invalidate();
		
		return "redirect:/main"; 
	}

	// 탈퇴 --------------------------------------------------------------------

	@GetMapping("/memberDelete")
	public String deleteMember(HttpSession session, HttpServletResponse response) throws IOException {

		String memId = (String) session.getAttribute("loginId");

	    // 로그인도 안하고 가입하려 하면 로그인 화면으로
	    if(memId == null) {
	        return "redirect:/members/login";
	    }

	    int result = memberService.deleteMember(memId); 

		if (result == 1) {
			session.invalidate(); // 세션 제거

	        // 탈퇴 시 띄울 alert창
	        response.setContentType("text/html; charset=utf-8");
	        PrintWriter out = response.getWriter();
	        out.println("<script>");
	        out.println(" alert('회원 탈퇴가 완료되었습니다.');");
	        out.println(" location.href='/main';");
	        out.println("</script>");
	        return null;  
	    }

	    return "redirect:/main";	
	}


}
