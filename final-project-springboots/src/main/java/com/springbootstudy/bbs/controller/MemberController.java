package com.springbootstudy.bbs.controller;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
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

	// 임시 메인 - 메인 생기면 삭제 예정
	@GetMapping("/fragments/main")
	public String main(Model model, HttpSession session) {

		// 로그인 정보가 있으면 model에 추가해서 template에서 사용 가능하게 함
		 MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
		 
		 if (loginUser != null) {
		 model.addAttribute("loginUser", loginUser);
		 }

		return "/fragments/main"; 
	}

	// 회원가입 -----------------------------------------------------------------

	// signUp.html - 창 띄우기
	@GetMapping("/views/member/signUp")
	public String signUp() {

		return "views/member/signUp";
	}

	// signUp.html - 회원가입 처리 기능
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
			Model model) {

		try {
			memberService.insertMember(
					memId, memPwd, memName, memTel, memEmail,
					memIp, memRoleIdx, memGradeIdx);

			return "redirect:/views/member/login";

		} catch (Exception e) {
			e.printStackTrace();

			model.addAttribute("errorMessage", "회원가입 실패하였습니다");
			return "views/member/signUp";
		}
	}

	// signUp.html - 중복 아이디 검사
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

	// login.html - 창 띄우기
	@GetMapping("/views/member/login")
	public String login() {

		return "/views/member/login";
	}
//	// 세션, 서블릿 리퀘스트를 넣고 -> 서비스 들고 옴(model로)
//
	// login.html - 로그인 처리 기능	
	@PostMapping("/views/member/login")
	public String login(@RequestParam("memId") String memId, @RequestParam("memPwd") String memPwd, 
			Model model, HttpSession session, RedirectAttributes ra
			) throws ServletException, IOException {
		
		// MemberService 클래스를 사용해 로그인 성공여부 확인
		int result = memberService.login(memId, memPwd);
		
		if(result == -1) { // 회원 아이디가 존재하지 않으면
			ra.addFlashAttribute("error", "존재하지 않는 아이디입니다.");
		    return "redirect:/member/loginForm";

			
		} else if(result == 0) { // 비밀번호가 틀리면
			ra.addFlashAttribute("error", "비밀번호가 틀립니다.");
		    return "redirect:/member/loginForm";
		}		
		
		// 로그인을 성공하면 회원 정보를 DB에서 가져와 세션에 저장한다.
		MemberVO memberVO = memberService.getMemberVO(memId);
		session.setAttribute("isLogin", true);
		session.setAttribute("loginId", memId);
		
		session.setAttribute("loginUser", memberVO);
		System.out.println("memberVO.name : " + memberVO.getMemName());
		
		return "redirect:/fragments/main"; 
	}

	// 비번찾기 -----------------------------------------------------------------
	// 회원정보 수정 / 삭제 기능이 구현 된 이후 구현할 예정!!

	// pwdFind.html - 창 띄우기
	@GetMapping("/views/member/pwdFind")
	public String pwdFind() {

		return "/views/member/pwdFind"; // ## 수정 - main 생기면 바꿔
	}

	// 회원정보 수정 -----------------------------------------------------------------

	// memberUpdate.html - 창 띄우기
	@GetMapping("/views/member/memberUpdate")
	public String memberUpdate(HttpSession session, Model model) {

		/*
		 * 로그인 한 뒤 -> 회원 정보를 받아서 페이지를 열어야 함
		 * 안그러면 500 에러
		 * 회원정보를 받을려면 로그인 할 때 값을 담은 session과 model이 필요함
		 */
		// 로그인 정보 가져오기
		MemberVO memberVO = (MemberVO) session.getAttribute("loginUser");

		System.out.println("🔍 memberUpdate 접근!");
		System.out.println("📝 세션 ID: " + session.getId());
		System.out.println("📝 세션에 저장된 loginUser: " + memberVO);
		System.out.println("📝 세션의 모든 속성: " +
				java.util.Collections.list(session.getAttributeNames()));

		// 로그인 안했으면 쫓아내기
		if (memberVO == null) {
			System.out.println("❌ memberVO가 NULL입니다! 로그인 페이지로 리다이렉트");
			return "redirect:/views/member/login";
		}

		model.addAttribute("memberVO", memberVO);

		return "views/member/memberUpdate";
	}

	// 로그아웃 -----------------------------------------------------------------
	
	@GetMapping("/memberLogout")
	public String logout(HttpSession session) {	
		
		session.invalidate();
		
		return "redirect:/fragments/main";
	}
	
	
	// 탈퇴 --------------------------------------------------------------------
	
	@GetMapping("/memberDelete")
	public String deleteMember(HttpSession session, HttpServletResponse response) throws IOException {

	    String memId = (String) session.getAttribute("loginId");

	    // 로그인도 안하고 가입하려 하면 로그인 화면으로
	    if(memId == null) {
	        return "redirect:/views/member/login";
	    }

	    int result = memberService.deleteMember(memId);

	    if(result == 1) {
	        session.invalidate(); // 세션 제거

	        // 탈퇴 시 띄울 alert창
	        response.setContentType("text/html; charset=utf-8");
	        PrintWriter out = response.getWriter();
	        out.println("<script>");
	        out.println(" alert('회원 탈퇴가 완료되었습니다.');");
	        out.println(" location.href='/';");
	        out.println("</script>");
	        return null;
	    }

	    return "redirect:/fragments/main";	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
