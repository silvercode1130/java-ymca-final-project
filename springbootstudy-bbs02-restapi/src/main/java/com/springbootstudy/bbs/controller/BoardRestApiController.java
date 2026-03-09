package com.springbootstudy.bbs.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springbootstudy.bbs.domain.Board;
import com.springbootstudy.bbs.domain.PagingBoard;
import com.springbootstudy.bbs.service.BoardService;

import lombok.extern.slf4j.Slf4j;


/* RestAPI 요청을 받는 RestController 클래스 정의
 * @RestController 애노테이션은 @Controller에 @ResponseBody가
 * 추가된 것과 동일하다. RestController의 주용도는 JSON으로 응답하는 것이다.  
 **/
@RestController
@Slf4j
public class BoardRestApiController {
	
	@Autowired
	private BoardService boardService;	
	
	
	// 게시 글 쓰기 폼과 게시 글 수정 폼은 필요 없음 - 프런트 단에서 뷰를 처리
	// 게시 글 수정 폼 요청시 게시 글 번호에 대한 비밀번호 체크 해야함/
		
	/* 게시글 리스트에 대한 HTTP GET 요청을 받는 메서드	
	 * @PathVariable 애노테이션을 사용해 no에 대해서 경로 변수를 사용함 
	 * @RestController 애노테이션이 클래스에 적용되었기 때문에 
	 * 이 메서드에서 반환되는 값은 JSON으로 직렬화되어 응답 본문에 포함된다.
	 * ResponseEntity 객체는 사용자 요청에 대한 응답 데이터를 포함하는 객체로
	 * HttpStatus, HttpHeaders, HttpBody를 포함하고 있어서 HTTP 상태
	 * 코드와 헤더 그리고 응답 본문에 포함되는 데이터를 제어할 수 있는 객체이다.	 
	 **/
	@GetMapping({"/boards/", "/boards/{pageNum}"})	
	public ResponseEntity<Map<String, Object>> boardList(
			@PathVariable(name="pageNum", required=false) Optional<Integer> optionalPageNum) {
		// pageNum 파라미터가 없는 경우 1 페이지 설정
		int pageNum = optionalPageNum.orElse(1);
		log.info("boardList() - pageNum : " + pageNum);
		
		// Service 클래스를 이용해 게시글 리스트를 가져온다.
		Map<String, Object> modelMap = boardService.boardList(pageNum);
		
		return ResponseEntity
					.ok()	// 정상처리 200 OK
					.body(modelMap); // 응답 본문 데이터
	}
	
	/* 게시글 상세보기에 대한 HTTP GET 요청을 받는 메서드
	 * @PathVariable 애노테이션을 사용해 no와 pageNum에 대한 경로 변수를 사용함
	 * pageNum 경로 변수가 없을 경우 ?
	 **/
	@GetMapping("/boards/{no}/{pageNum}")
	public ResponseEntity<Map<String, Object>> getBoard(
			@PathVariable("no") int no,
			@PathVariable(name="pageNum", required=false) int pageNum) {
		log.info("getBoard() - no : " + no + ", pageNum : " + pageNum);
		
		// 게시글 상세보기에서 게시글 조회 횟수 증가
		Board board = boardService.getBoard(no, true);
		
		Map<String, Object> modelMap = new HashMap<>();
		modelMap.put("board", board);
		modelMap.put("pageNum", pageNum);
		
		return ResponseEntity
					.ok()	// 정상처리 200 OK
					.body(modelMap); // 응답 본문 데이터
	}

	/* 게시글 추가에 대한 HTTP POST 요청을 받는 메서드
	 * @RequestBody는 요청 본문으로 들어오는 JSON이나 XML 데이터를 Board
	 * 객체로 변환한다. JSON이나 XML이 아니거나 포맷에 맞지 않으면 400 오류가 발생한다. 
	 **/
	@PostMapping("/boards")	
	public ResponseEntity<Board> addBoard(@RequestBody Board board) {
		log.info("addBoard() - title : " + board.getTitle());
		
		boardService.addBoard(board);
		
		// HTTP 상태 코드를 201 CREATED로 설정하여 응답
		return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(boardService.getBoard(board.getNo(), false));
	}
	
	// 게시글 수정에 대한 HTTP PATCH 요청을 받는 메서드
	// PATCH 요청은 요청 본문에 데이터가 들어오므로 PathVariable 사용 못함 - 별도 객체가 필요
	@PatchMapping("/boards")
	public ResponseEntity<Map<String, Object>> updateBoard(			
			@RequestBody PagingBoard pagingBoard) {		
		log.info("updateBoard() - title : " + pagingBoard.getBoard().getTitle());
		log.info("updateBoard() - pageNum : " + pagingBoard.getPageNum());
		
		/* 비밀번호가 틀렸을 때에 대한 응답 처리가 필요함
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
		*/
		
		// 비밀번호가 맞으면 DB 테이블에서 no에 해당하는 게시글 정보를 수정한다.		
		boardService.updateBoard(pagingBoard.getBoard());
		
		// 게시글 수정에서 게시글 조회 횟수 증가하지 않음
		Board board = boardService.getBoard(pagingBoard.getBoard().getNo(), false);
		
		Map<String, Object> modelMap = new HashMap<>();		
		modelMap.put("result", "ok");
		modelMap.put("board", board);
		modelMap.put("pageNum", pagingBoard.getPageNum());
		
		return ResponseEntity
					.ok()	// 정상처리 200 OK
					.body(modelMap); // 응답 본문 데이터
	}
	
	// 게시글 삭제에 대한 HTTP DELETE 요청을 받는 메서드
	@DeleteMapping("/boards/{no}/{pass}/{pageNum}")
	public ResponseEntity<Map<String, Object>> deleteBoard(@PathVariable("no") int no, 
			@PathVariable("pass") String pass,
			@PathVariable(value="pageNum", required=false) int pageNum) {
		log.info("deleteBoard() - no : " + no + ", pageNum : " + pageNum);
		
		/* 비밀번호가 틀렸을 때에 대한 응답 처리가 필요함
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
		*/
		// 비밀번호가 맞으면 DB 테이블에서 no에 해당하는 게시글을 삭제한다.
		boardService.deleteBoard(no);
		
		Map<String, Object> modelMap = new HashMap<>();
		modelMap.put("result", "ok");
		modelMap.put("pageNum", pageNum);
		
		return ResponseEntity
					.ok()	// 정상처리 200 OK
					.body(modelMap); // 응답 본문 데이터
	}
}
