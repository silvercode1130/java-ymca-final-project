package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.springbootstudy.bbs.domain.Board;

/* 이전에는 DAO(Data Access Object) 클래스에 @Repository 애노테이션을
 * 적용하여 해당 클래스가 DB 작업을 하는 클래스 임을 명시하고
 * MyBatis의 Mapper 인터페이스나 XML 맵퍼를 통해서 DB와 통신하였다.
 * 이때 DAO 클래스에서 XML 맵퍼 파일에 정의한 namespace라는 속성과
 * 맵퍼 파일 안에 작성한 SQL 쿼리(맵핑 구문)의 id를 조합해 SQL 쿼리를
 * 호출했었다. 하지만 요즘에는 아래와 같이 자바 인터페이스에 @Mapper
 * 애노테이션을 적용하고 이 인터페이스의 메서드에 @Select, @Insert, 
 * @Update, @Delete 등의 애노테이션을 지정해 쿼리를 직접 맵핑할 수도 있다.
 * 또한 별도의 XML 맵퍼 파일을 만들고 그 안에 SQL 쿼리를 정의하여 이 맵핑
 * 구문을 호출할 수도 있다. 이때 한 가지 주의할 점은 @Mapper 애노테이션을
 * 적용한 인터페이스와 XML 맵퍼 파일은 namespace라는 속성으로 연결되기
 * 때문에 XML 맵퍼 파일의 namespace를 정의할 때 맵퍼 인터페이스의 완전한
 * 클래스 이름과 동일한 namespace를 사용해야 한다는 것이다.
 * 
 * @Mapper는 MyBatis 3.0부터 지원하는 애노테이션으로 이 애노테이션이 붙은
 * 인터페이스는 별도의 구현 클래스를 작성하지 않아도 MyBatis 맵퍼로 인식해
 * 스프링 Bean으로 등록되며 Service 클래스에서 주입 받아 사용할 수 있다. 
 **/
@Mapper
public interface BoardMapper {

	/* 한 페이지에 해당하는 게시글 리스트를 DB 테이블에서 읽어와 반환하는 메서드
	 * 
	 * 맵퍼 XML의 맵핑 구문을 호출하면서 전달해야할 파라미터가 여러 개일 때
	 * 다음과 같이 @Param("파라미터 이름") 애노테이션을 사용해 맵핑 구문에서
	 * 사용할 파라미터 이름을 지정하면 파라미터 이름을 키로 지정해 Map 객체에
	 * 저장되고 맵핑 구문으로 전달된다.
	 **/
	public List<Board> boardList(
			@Param("startRow") int startRow, @Param("num") int num);

	// DB 테이블에 등록된 전체 게시글 수를 읽어와 반환하는 메서드	 
	public int getBoardCount();
		
	// DB 테이블에서 no에 해당하는 게시글을 읽어와 Board 객체로 반환하는 메서드
	public Board getBoard(int no);
		
	// 게시글을 Board 객체로 받아서 DB 테이블에 추가하는 메서드
	public void insertBoard(Board board);
		
	// no에 해당하는 비밀번호를 DB 테이블에서 읽어와 반환하는 메서드
	public String isPassCheck(int no);
		
	// 수정된 게시글을 Board 객체로 받아서 DB 테이블에서 수정하는 메서드
	public void updateBoard(Board board);	
	
	//no에 해당 하는 게시글을 DB 테이블에서 삭제하는 메서드
	public void deleteBoard(int no);

	// no에 해당하는 게시글의 읽은 횟수를 DB 테이블에서 증가시키는 메서드
	public void incrementReadCount(int no);
	
}
