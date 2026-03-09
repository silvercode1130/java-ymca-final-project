package com.springbootstudy.bbs.controller;

import java.io.PrintWriter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springbootstudy.bbs.domain.Board;
import com.springbootstudy.bbs.service.BoardService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/* Spring MVC Controller 클래스 정의
 * @Controller 애노테이션이 적용된 클래스의 메서드는 기본적으로 뷰의 이름을 반환한다.
 **/
@Controller
@Slf4j
public class BoardController {
	
	@Autowired
	private BoardService boardService;	
	
	/* 게시글 리스트 요청을 처리하는 메서드
	 * "/", "/boardList" 로 들어오는 HTTP GET 요청을 처리하는 메서드
	 * 
	 * 스프링은 컨트롤러에서 모델에 데이터를 담을 수 있는 다양한 방법을 제공하며
	 * 아래와 같이 파라미터에 Model을 지정하는 방식이 많이 사용된다. 
	 * @RequestMapping 애노테이션이 적용된 메서드의 파라미터에 Model
	 * 을 지정하면 스프링이 이 메서드를 호출하면서 Model 타입의 객체를 넘겨준다.
	 * 우리는 Model을 받아 이 객체에 결과 데이터를 담기만 하면 뷰에서 사용할 수 있다.
	 * 
	 * @RequestParam 애노테이션을 이용해 pageNum이라는 요청 파라미터를 받도록 하였다. 
	 * 아래에서 pageNum이라는 요청 파라미터가 없을 경우 required=false를 지정해 필수
	 * 조건을 주지 않았고 기본 값을  defaultValue="1"로 지정해 메서드의 파라미터인
	 * pageNum으로 받을 수 있도록 하였다. defaultValue="1"이 메서드의 파라미터인
	 * pageNum에 바인딩될 때 스프링이 int 형으로 형 변환하여 바인딩 시켜준다.
	 **/
	@GetMapping({"/", "/boardList"})
	public String boardList(Model model, 
			@RequestParam(value="pageNum", required=false, 
			defaultValue="1") int pageNum) {
		
		// Service 클래스를 이용해 게시글 리스트를 가져온다.
		Map<String, Object> modelMap = boardService.boardList(pageNum);
		
		/* 파라미터로 받은 모델 객체에 뷰로 보낼 모델을 저장한다.
		 * 모델에는 도메인 객체나 비즈니스 로직을 처리한 결과를 저장한다. 
		 **/		
		model.addAllAttributes(modelMap);

		// 페이지 모듈화로 content 페이지가 "/templates/views" 폴더로 이동함
		return "views/boardList";
	}
	
	/* 게시글 상세보기 요청 처리 메서드
	 * "/boardDetail"로 들어오는 HTTP GET 요청을 처리하는 메서드
	 * 
	 * 페이징 기능 연동을 위해서 @RequestParam 애노테이션을 이용해 pageNum이라는
	 * 요청 파라미터를 받도록 하였다. 아래에서 pageNum이라는 요청 파라미터가 없을
	 * 경우 게시글 리스트의 첫 페이지로 보내기 위해서 defaultValue="1"로 지정해
	 * 메서드의 파라미터인 pageNum으로 받을 수 있도록 하였다 
	 **/
	@GetMapping("/boardDetail")
	public String getBoard(Model model, @RequestParam("no") int no,
			@RequestParam(value="pageNum", defaultValue="1") int pageNum) {
		
		/* 게시글 상세보기는 게시글 조회에 해당하므로 no에 해당하는 게시글 정보를
		 * 읽어오면서 두 번째 인수에 true를 지정해 게시글 읽은 횟수를 1 증가시킨다.
		 **/
		Board board = boardService.getBoard(no, true);
		
		// no에 해당하는 게시글 정보와 pageNum을 모델에 저장한다.
		model.addAttribute("board", board);
		model.addAttribute("pageNum", pageNum);		
		
		return "views/boardDetail";
	}

	/* 게시글 쓰기 폼 요청 처리 메서드
	 * "/addBoard"로 들어오는 HTTP GET 요청을 처리하는 메서드
	 **/
	@GetMapping("/addBoard")
	public String addBoard() {
		// 게시글 쓰기 폼은 모델이 필요 없기 때문에 뷰만 반환 
		return "views/writeForm";
	}
	
	/* 게시글 쓰기 폼에서 들어오는 게시글 쓰기 요청을 처리하는 메서드
	 * "/addBoard"로 들어오는 HTTP POST 요청을 처리하는 메서드
	 **/
	@PostMapping("/addBoard")	
	public String addBoard(Board board) {
		log.info("title : ", board.getTitle());
		boardService.addBoard(board);
		
		// 게시글 쓰기가 완료되면 게시글 리스트로 리다이렉트 시킨다.
		return "redirect:boardList";
	}

	/* 게시글 수정 폼 요청을 처리하는 메서드
	 * "/updateForm"으로 들어오는 HTTP POST 요청을 처리하는 메서드	 
	 * 
	 * 페이징 기능 연동을 위해서 @RequestParam 애노테이션을 이용해 pageNum이라는
	 * 요청 파라미터를 받도록 하였다. 아래에서 pageNum이라는 요청 파라미터가 없을
	 * 경우 게시글 리스트의 첫 페이지로 보내기 위해서 defaultValue="1"로 지정해
	 * 메서드의 파라미터인 pageNum으로 받을 수 있도록 하였다 
	 **/
	@PostMapping("/updateForm")
	public String updateBoard(Model model, 
			HttpServletResponse response, PrintWriter out,
			@RequestParam("no") int no, @RequestParam("pass") String pass,
			@RequestParam(value="pageNum", defaultValue="1") int pageNum) {
		
		// 사용자가 입력한 비밀번호가 틀리면 자바스크립트로 응답
		boolean isPassCheck = boardService.isPassCheck(no, pass);		
		if(! isPassCheck) {
			response.setContentType("text/html; charset=utf-8");				
			out.println("<script>");
			out.println("	alert('비밀번호가 맞지 않습니다.');");
			out.println("	history.back();");
			out.println("</script>");

			// null을 반환하면 위에서 스트림에 출력한 자바스크립트 코드가 응답된다. 
			return null;
		}		
		
		/* 수정 폼 요청은 게시글 조회가 아니므로 비밀번호가 맞으면 no에 해당하는
		 * 게시글 정보를 읽어오면서 두 번째 인수로 false를 지정해 게시글 읽은 횟수를
		 * 증가시키지 않는다. 
		 **/
		Board board = boardService.getBoard(no, false);
		
		// no에 해당하는 게시글 정보와 pageNum을 모델에 저장한다.
		model.addAttribute("board", board);
		model.addAttribute("pageNum", pageNum);		
		return "views/updateForm";		
	}

	/* 게시글 수정 폼에서 들어오는 게시글 수정 요청을 처리하는 메서드
	 * "/update"로 들어오는 HTTP POST 요청을 처리하는 메서드
	 *
	 * 페이징 기능 연동을 위해서 @RequestParam 애노테이션을 이용해 pageNum이라는
	 * 요청 파라미터를 받도록 하였다. 아래에서 pageNum이라는 요청 파라미터가 없을
	 * 경우 게시글 리스트의 첫 페이지로 보내기 위해서 defaultValue="1"로 지정해
	 * 메서드의 파라미터인 pageNum으로 받을 수 있도록 하였다. 또한 리다이렉트할 때
	 * pageNum을 파라미터로 보내기 위해서 RedirectAttributes 객체를 파라미터로 지정했다.
	 **/
	@PostMapping("/update")
	public String updateBoard(Board board, RedirectAttributes reAttrs,
			@RequestParam(value="pageNum", defaultValue="1") int pageNum,
			HttpServletResponse response, PrintWriter out) {
		
		// 사용자가 입력한 비밀번호가 틀리면 자바스크립트로 응답
		boolean isPassCheck = boardService.isPassCheck(board.getNo(), board.getPass());		
		if(! isPassCheck) {
			response.setContentType("text/html; charset=utf-8");				
			out.println("<script>");
			out.println("	alert('비밀번호가 맞지 않습니다.');");
			out.println("	history.back();");
			out.println("</script>");
			
			// null을 반환하면 위에서 스트림에 출력한 자바스크립트 코드가 응답된다.
			return null;
		}
				
		// 비밀번호가 맞으면 DB 테이블에서 no에 해당하는 게시글 정보를 수정한다.		
		boardService.updateBoard(board);

		/* 클라이언트 요청을 처리한 후 리다이렉트 해야 할 경우 아래와 같이 redirect:
		 * 접두어를 붙여 뷰 이름을 반환하면 된다. 뷰 이름에 redirect 접두어가 붙으면
		 * HttpServletResponse를 사용해서 지정한 경로로 Redirect 된다. 
		 * redirect 접두어 뒤에 경로를 지정할 때 "/"로 시작하면 ContextRoot를
		 * 기준으로 절대 경로 방식으로 Redirect 된다. "/"로 시작하지 않으면 현재 
		 * 경로를 기준으로 상대 경로로 Redirect 된다. 또한 다른 사이트로 Redirect
		 * 되기를 원한다면 redirect:http://사이트 주소를 지정한다.
		 * 
		 * Redirect 되는 경우 주소 끝에 파라미터를 지정해 GET방식의 파라미터로
		 * 전송할 수 있지만 스프링프레임워크가 지원하는 RedirectAttributs객체를
		 * 이용하면 한 번만 사용할 임시 데이터와 지속적으로 사용할 파라미터를
		 * 구분해 지정할 수 있다.
		 * 
		 * 아래와 같이 RedirectAttributs의 addAttribute() 메서드를 사용해
		 * 지속적으로 사용할 파라미터를 지정하면 자동으로 주소 뒤에 파라미터로
		 * 추가되며 addFlashAttribute() 메서드를 사용해 파라미터로 지정하면
		 * 한 번만 사용할 수 있고 이후에는 주소 뒤에 파라미터로 추가되지 않는다. 
		 * addAttribute() 메서드를 사용해 파라미터로 지정한 데이터는 페이지를
		 * 새로 고침해도 계속해서 주소 뒤에 파라미터로 남아있지만 addFlashAttribute()
		 * 메서드를 사용해 지정한 파라미터는 사라지기 때문에 1회성으로 필요한
		 * 데이터를 addFlashAttribute() 메서드를 사용해 지정하면 편리하다.
		 * 
		 * 파라미터에 한글이 포함되는 경우 URLEncoding을 java.net 패키지의
		 * URLEncoder 클래스를 이용해 코드로 인코딩 처리를 해야 하지만
		 * application.properties에 인코딩 관련 설정이 되어 있기 때문에 별도로
		 * 처리할 필요가 없다. 
		 **/
		reAttrs.addAttribute("pageNum", pageNum);
		reAttrs.addFlashAttribute("test1", "1회성 파라미터");
		return "redirect:boardList";
	}
	
	/* 게시글 삭제 요청을 처리 메서드
	 *	"/delete"로 들어오는 HTTP POST 요청을 처리하는 메서드
	 **/
	@PostMapping("/delete")
	public String deleteBoard(RedirectAttributes reAttrs,
			HttpServletResponse response, PrintWriter out,
			@RequestParam("no") int no, @RequestParam("pass") String pass,
			@RequestParam(value="pageNum", defaultValue="1") int pageNum) {
		
		// 사용자가 입력한 비밀번호가 틀리면 자바스크립트로 응답
		boolean isPassCheck = boardService.isPassCheck(no, pass);		
		if(! isPassCheck) {
			response.setContentType("text/html; charset=utf-8");				
			out.println("<script>");
			out.println("	alert('비밀번호가 맞지 않습니다.');");
			out.println("	history.back();");
			out.println("</script>");
			
			return null;
		}		
		
		// 비밀번호가 맞으면 DB 테이블에서 no에 해당하는 게시글을 삭제한다.
		boardService.deleteBoard(no);
		
		// RedirectAttributes를 이용해 리다이렉트 할 때 필요한 파라미터를 지정 
		reAttrs.addAttribute("pageNum", pageNum);
		
		/* 게시글 삭제가 완료되면 게시글 리스트로 리다이렉트 시킨다.
		 * 클라이언트 요청을 처리한 후 리다이렉트 해야 할 경우 아래와 같이 redirect:
		 * 접두어를 붙여 뷰 이름을 반환하면 된다. 뷰 이름에 redirect 접두어가 붙으면
		 * HttpServletResponse를 사용해서 지정한 경로로 Redirect 된다.
		 **/ 		
		return "redirect:boardList";
	}
}
