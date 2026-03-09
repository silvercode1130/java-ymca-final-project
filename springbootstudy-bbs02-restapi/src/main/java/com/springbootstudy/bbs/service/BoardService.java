package com.springbootstudy.bbs.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.domain.Board;
import com.springbootstudy.bbs.mapper.BoardMapper;

import lombok.extern.slf4j.Slf4j;

// BoardService 클래스가 서비스 계층의 스프링 빈(Bean) 임을 정의 
@Service
@Slf4j
public class BoardService {
	
	// DB 작업에 필요한 BoardMapper 객체를 의존성 주입 설정 
	@Autowired
	private BoardMapper boardMapper;
		
	// 한 페이지에 출력할 게시글의 수를 상수로 선언
	private static final int PAGE_SIZE = 10;
	
	/* 한 페이지에 출력할 페이지 그룹의 수를 상수로 선언
	 * [이전] 1 2 3 4 5 6 7 8 9 10 [다음]
	 **/
	private static final int PAGE_GROUP = 10;	
	
	/* 한 페이지에 출력할 게시글 리스트와 페이징 처리에 필요한 데이터를
	 * Map 객체로 반환하는 메서드 
	 **/
	public Map<String, Object> boardList(int pageNum) {
		log.info("BoardService: boardList(int pageNum)");
		
		// 요청 파라미터의 pageNum을 현재 페이지로 설정
		int currentPage = pageNum;
		
		/* 현재 페이지에 해당하는 게시글 리스트의 첫 번째 행의 값을 계산
		 * 
		 * MySQL에서 검색된 게시글 리스트의 row에 대한 index는 0부터 시작한다.
		 * 현재 페이지가 1일 경우 startRow는 0, 2페이지일 경우 startRow는 10이 된다.
		 * 
		 * 예를 들어 3페이지에 해당하는 게시글 리스트를 가져 온다면 한 페이지에 
		 * 출력할 게시글 리스트의 수가 10개로 지정되어 있으므로 startRow는 20이 된다. 
		 * 즉 아래의 공식에 의해 startRow(20) = (3 - 1) * 10;
		 * 1페이지 startRow = 0, 2 페이지 startRow = 10이 된다.
		 **/		
		int startRow = (currentPage - 1) * PAGE_SIZE;
		log.info("startRow : " + startRow);
		
		// BoardMapper를 이용해 전체 게시글 수를 가져온다.		
		int listCount = boardMapper.getBoardCount();
		
		// 현재 페이지에 해당하는 게시글 리스트를 BoardMapper를 이용해 DB에서 읽어온다.
		List<Board> boardList = boardMapper.boardList(startRow, PAGE_SIZE);		
		
		/* 페이지 그룹 이동 처리를 위해 전체 페이지 수를 계산 한다. 
		 * [이전] 11 12 13...   또는   ... 8 9 10 [다음]과 같은 페이지 이동 처리
		 * 
		 * 전체 페이지 = 전체 게시글 수 / 한 페이지에 표시할 게시글 수가 되는데 
		 * 이 계산식에서 나머지가 존재하면 전체 페이지 수는 전체 페이지 + 1이 된다.
		 **/	
		int pageCount = 
				listCount / PAGE_SIZE + (listCount % PAGE_SIZE == 0 ? 0 : 1);
		
		/* 페이지 그룹 처리를 위해 페이지 그룹별 시작 페이지와 마지막 페이지를 계산
		 * 
		 * 첫 번째 페이지 그룹에서 페이지 리스트는 1 ~ 10이 되므로 currentPage가
		 * 1 ~ 10 사이에 있으면 startPage는 1이 되고 11 ~ 20 사이면 11이 된다.
		 * 페이지 그룹 별 시작 페이지 : 1, 11, 21, 31...
		 * 
		 * 정수형 연산의 특징을 이용해 startPage를 아래와 같이 구할 수 있다.
		 * 아래 연산식으로 계산된 결과를 보면 현재 그룹의 마지막 페이지일 경우
		 * startPage가 다음 그룹의 시작 페이지가 나오게 되므로 삼항 연자자를
		 * 사용해 현재 페이지가 속한 그룹의 startPage가 되도록 조정 하였다.
		 * 즉 currentPage가 10일 경우 다음 페이지 그룹의 시작 페이지가 되므로
		 * 삼항 연산자를 사용하여 PAGE_GROUP으로 나눈 나머지가 0이면
		 * PAGE_GROUP을 차감하여 현재 페이지 그룹의 시작 페이지가 되도록 하였다.
		 **/
		int startPage = (currentPage / PAGE_GROUP) * PAGE_GROUP + 1
				- (currentPage % PAGE_GROUP == 0 ? PAGE_GROUP : 0);		
					
		// 현재 페이지 그룹의 마지막 페이지 : 10, 20, 30...
		int endPage = startPage + PAGE_GROUP - 1;			
		
		/* 위의 식에서 endPage를 구하게 되면 endPage는 항상 PAGE_GROUP의
		 * 크기만큼 증가(10, 20, 30 ...) 되므로 맨 마지막 페이지 그룹의 endPage가
		 * 정확하지 못할 경우가 발생하게 된다. 다시 말해 전체 페이지가 53페이지라고
		 * 가정하면 위의 식에서 계산된 endPage는 60 페이지가 되지만 실제로 
		 * 60페이지는 존재하지 않는 페이지이므로 문제가 발생하게 된다.
		 * 그래서 맨 마지막 페이지에 대한 보정이 필요하여 아래와 같이 endPage와
		 * pageCount를 비교하여 현재 페이지 그룹에서 endPage가 pageCount 보다
		 * 크다면 pageCount를 endPage로 지정 하였다. 즉 현재 페이지 그룹이
		 * 마지막 페이지 그룹이면 endPage는 전체 페이지 수가 되도록 지정한 것이다.
		 **/
		if(endPage > pageCount) {
			endPage = pageCount;
		}	
		
		/* View 페이지에서 필요한 데이터를 Map에 저장한다.
		 * 현재 페이지, 전체 페이지 수, 페이지 그룹의 시작 페이지와 마지막 페이지
		 * 게시글 리스트의 수, 한 페이지에 출력할 게시글 리스트의 데이터를 Map에
		 * 저장해 컨트롤러로 전달한다.
		 **/
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		
		modelMap.put("bList", boardList);
		modelMap.put("pageCount", pageCount);
		modelMap.put("startPage", startPage);
		modelMap.put("endPage", endPage);
		modelMap.put("currentPage", currentPage);
		modelMap.put("listCount", listCount);
		modelMap.put("pageGroup", PAGE_GROUP);
		
		return modelMap;
	}
	
	/* no에 해당하는 게시글을 읽어와 반환하는 메서드
	 *  
 	 * isCount == true : 게시 상세보기 요청, false : 그 외 요청임 
	 **/
	public Board getBoard(int no, boolean isCount) {	
		log.info("BoardService: getBoard(int no, boolean isCount)");
		
		// 게시글 상세보기 요청만 게시글 읽은 횟수를 증가시킨다.
		if(isCount) {
			boardMapper.incrementReadCount(no);
		}
		return boardMapper.getBoard(no);
	}
	
	// 게시글 정보를 추가하는 메서드
	public void addBoard(Board board) {
		log.info("BoardService: addBoard(Board board)");
		boardMapper.insertBoard(board);
	}

	/* 게시글 수정과 삭제 할 때 비밀번호가 맞는지 체크하는 메서드	
	 * 
	 * - 게시글의 비밀번호가 맞으면 : true를 반환
	 * - 게시글의 비밀번호가 맞지 않으면 : false를 반환
	 **/
	public boolean isPassCheck(int no, String pass) {	
		log.info("BoardService: isPassCheck(int no, String pass)");
		boolean result = false;
		
		// BoardMapper를 이용해 DB에서 no에 해당하는 비밀번호를 읽어온다.
		String dbPass = boardMapper.isPassCheck(no);
		
		if(dbPass.equals(pass)) {
			result = true;		
		}
		
		// 비밀번호가 맞으면 true, 맞지 않으면 false가 반환된다.
		return result;
	}	

	// 게시글을 수정하는 메서드
	public void updateBoard(Board board) {
		log.info("BoardService: updateBoard(Board board)");
		boardMapper.updateBoard(board);
	}
	
	// no에 해당하는 게시글을 삭제하는 메서드
	public void deleteBoard(int no) {
		log.info("BoardService: deleteBoard(int no)");
		boardMapper.deleteBoard(no);
	}	
}
