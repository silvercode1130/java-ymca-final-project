package com.springbootstudy.bbs.controller;

import java.io.IOException;
import java.io.PrintWriter;

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
   public String loginForm(@RequestParam(value = "redirect", required = false) String redirect, 
		   		HttpServletResponse response, HttpSession session, Model model) {

       // 뒤로가기 했을 때 로그인 안풀리는 기능
       response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
       response.setHeader("Pragma", "no-cache");
       response.setDateHeader("Expires", 0);
       
       // 새로고침 했을 때 로그인 안풀리는 기능
       if (session.getAttribute("loginUser") != null) {
           return "redirect:/main"; 
       }
      
       String msg = (String) session.getAttribute("loginMsg");

       // 로그인 메시지
       if (msg != null) {
           model.addAttribute("loginMsg", msg);
           session.removeAttribute("loginMsg"); 
       }
       
       // 로그인(인터셉터) 후 보던 페이지로 돌아오기
       if (redirect != null) model.addAttribute("redirect", redirect);


       return "views/member/login";
   }
   
   // 세션, 서블릿 리퀘스트를 넣고 -> 서비스 들고 옴(model로)
   // login.html - 로그인 처리 기능
   @PostMapping("/members/login")
   public String login(@RequestParam("memId") String memId,
                       @RequestParam("memPwd") String memPwd,
                       @RequestParam(value = "redirect", required = false) String redirect,
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
       session.setAttribute("loginUser", memberVO); // 로그인 세션!! 
       
       // 결제용 index 추가
       session.setAttribute("memIdx", memberVO.getMemIdx()); 

       System.out.println("memberVO.name : " + memberVO.getMemName());

       return "redirect:/main";
   }

   // 로그아웃 -----------------------------------------------------------------

   @RequestMapping("/members/logout")
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
         
         String gradeName = memberMapper.selectGradeNameByMemId(memberVO.getMemId());
         
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

          //memberMapper.update(vo);   
          memberService.updateMember(vo);

          // 수정된 정보로 저장
          MemberVO updated = memberMapper.selectOneFromId(vo.getMemId()); 
          session.setAttribute("loginUser", updated);

          return "redirect:/main"; 
      } 
      
   // 비밀번호 찾기 -----------------------------------------------------------------
      
      // 창 띄우기
      @GetMapping("/members/pwdFind")
      public String pwdFind() { 
    	  
    	  return "/views/member/pwdFind"; 
      }
      
      // 그 외 기능들
      @PostMapping("/members/pwdFind")
      public String pwdFind(@RequestParam(value="memId", required=false) String memId,
              				@RequestParam(value="memTel", required=false) String memTel,
              				RedirectAttributes ra) {

          MemberVO member = memberService.findByIdAndTel(memId, memTel);

          // 값을 입력하지 않고 버튼을 누른 경우
          if(memId == null || memTel == null) {
        	    return "redirect:/members/pwdFind";
    	  }
          
          // 일치하지 않는 값을 가진 회원이 없는 경우
          if(member == null) {
              ra.addFlashAttribute("idMsg", "아이디 또는 전화번호가 일치하지 않습니다");
              ra.addFlashAttribute("telMsg", "아이디 또는 전화번호가 일치하지 않습니다");
          } else {
              ra.addFlashAttribute("verifyMsg", memTel + "로 인증번호가 전송되었습니다");
          }

          return "redirect:/members/pwdFind";
      }


}