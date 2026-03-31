package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.springbootstudy.bbs.domain.BoardTypeVO;
import com.springbootstudy.bbs.domain.BoardVO;
import com.springbootstudy.bbs.domain.ReplyVO;

@Mapper
public interface BoardMapper {

    // ── 게시판 타입 ──────────────────────────────────────────
    List<BoardTypeVO> findAllBoardTypes();

    // typeCode로 단건 조회
    BoardTypeVO findBoardTypeByCode(@Param("typeCode") String typeCode);

    // ── 게시글 ───────────────────────────────────────────────
    // 목록 (typeCode 기반)
    List<BoardVO> findBoards(
            @Param("typeCode") 	String typeCode,
            @Param("keyword")  	String keyword,
            @Param("searchType")String searchType
    );

    // 상세
    BoardVO findBoardById(@Param("boardIdx") Long boardIdx);

    // 조회수 +1
    void increaseViewCount(@Param("boardIdx") Long boardIdx);

    // 등록
    int insertBoard(BoardVO board);

    // 수정
    int updateBoard(BoardVO board);

    // 삭제 (soft delete)
    int deleteBoard(@Param("boardIdx") Long boardIdx);

    // 댓글 수
    int countRepliesByBoard(@Param("boardIdx") Long boardIdx);

    // ── 댓글 ─────────────────────────────────────────────────
    List<ReplyVO> findRepliesByBoard(@Param("boardIdx") Long boardIdx);

    // 원댓 등록 (ref = 자기 자신 idx, step=0, depth=0)
    int insertReply(ReplyVO reply);

    // 대댓 등록 전 step 밀기
    void shiftReplyStep(
            @Param("boardIdx") Long boardIdx,
            @Param("ref")      int  ref,
            @Param("step")     int  step
    );

    // 단건 조회 (대댓 처리용)
    ReplyVO findReplyById(@Param("replyIdx") Long replyIdx);

    // 원댓 ref 업데이트 (insert 후 자기 idx로 세팅)
    void updateReplyRef(ReplyVO reply);

    // 삭제 (soft delete)
    int deleteReply(@Param("replyIdx") Long replyIdx);
}
