package com.springbootstudy.bbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.domain.BoardTypeVO;
import com.springbootstudy.bbs.domain.BoardVO;
import com.springbootstudy.bbs.domain.ReplyVO;
import com.springbootstudy.bbs.mapper.BoardMapper;

@Service
public class BoardService {

    @Autowired
    private BoardMapper boardMapper;

    // ── 게시판 타입 ──────────────────────────────────────────
    public List<BoardTypeVO> getBoardTypes() {
        return boardMapper.findAllBoardTypes();
    }

    public BoardTypeVO getBoardTypeByCode(String typeCode) {
        return boardMapper.findBoardTypeByCode(typeCode);
    }

    // ── 게시글 목록 ──────────────────────────────────────────
    public List<BoardVO> getBoards(String typeCode, String keyword) {
        return boardMapper.findBoards(typeCode, keyword, null);
    }
    
    public List<BoardVO> getBoards(String typeCode, String keyword, String searchType) {
        return boardMapper.findBoards(typeCode, keyword, searchType);
    }

    // ── 게시글 상세 (조회수 포함) ─────────────────────────────
    public BoardVO getBoardDetail(Long boardIdx) {
        boardMapper.increaseViewCount(boardIdx);
        return boardMapper.findBoardById(boardIdx);
    }

    // ── 게시글 등록 ──────────────────────────────────────────
    public int writeBoard(BoardVO board) {
        return boardMapper.insertBoard(board);
    }

    // ── 게시글 수정 ──────────────────────────────────────────
    public int editBoard(BoardVO board) {
        return boardMapper.updateBoard(board);
    }

    // ── 게시글 삭제 ──────────────────────────────────────────
    public int removeBoard(Long boardIdx) {
        return boardMapper.deleteBoard(boardIdx);
    }

    // ── 댓글 목록 ────────────────────────────────────────────
    public List<ReplyVO> getReplies(Long boardIdx) {
        return boardMapper.findRepliesByBoard(boardIdx);
    }

    // ── 댓글 등록 ────────────────────────────────────────────
    // parentReplyIdx == null 이면 원댓, 있으면 대댓
    public int writeReply(ReplyVO reply, Long parentReplyIdx) {
        if (parentReplyIdx == null) {
            // 원댓: ref/step/depth 모두 0으로 insert 후 ref = 자기 idx로 update
            reply.setReplyRef(0);
            reply.setReplyStep(0);
            reply.setReplyDepth(0);
            boardMapper.insertReply(reply);
            // ref를 자기 자신 idx로 세팅 (원댓 그룹 식별자)
            ReplyVO update = new ReplyVO();
            update.setReplyIdx(reply.getReplyIdx());
            update.setReplyRef(reply.getReplyIdx().intValue());
            update.setReplyStep(0);
            update.setReplyDepth(0);
            update.setBoardIdx(reply.getBoardIdx());
            update.setMemIdx(reply.getMemIdx());
            update.setReplyContent(reply.getReplyContent());
            update.setReplyIp(reply.getReplyIp());
            boardMapper.updateReplyRef(update);
            return 1;
        } else {
            // 대댓: 부모 댓글 정보 조회
            ReplyVO parent = boardMapper.findReplyById(parentReplyIdx);
            int ref   = parent.getReplyRef();
            int step  = parent.getReplyStep();
            int depth = parent.getReplyDepth();

            // 같은 ref 그룹에서 step > 부모 step 인 것들 +1 밀기
            boardMapper.shiftReplyStep(reply.getBoardIdx(), ref, step);

            reply.setReplyRef(ref);
            reply.setReplyStep(step + 1);
            reply.setReplyDepth(depth + 1);
            return boardMapper.insertReply(reply);
        }
    }

    // ── 댓글 삭제 ────────────────────────────────────────────
    public int removeReply(Long replyIdx) {
        return boardMapper.deleteReply(replyIdx);
    }

    // ── 댓글 수 ──────────────────────────────────────────────
    public int getReplyCount(Long boardIdx) {
        return boardMapper.countRepliesByBoard(boardIdx);
    }
}
