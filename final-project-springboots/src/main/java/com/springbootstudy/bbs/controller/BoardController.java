package com.springbootstudy.bbs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springbootstudy.bbs.domain.BoardTypeVO;
import com.springbootstudy.bbs.domain.BoardVO;
import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.domain.ReplyVO;
import com.springbootstudy.bbs.service.BoardService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;

    // ── 게시판 목록 (전체 or 카테고리별) ─────────────────────
    // /boards → 전체 목록 (커뮤니티 홈)
    @GetMapping
    public String home(
            @RequestParam(value = "keyword",    required = false) String keyword,
            @RequestParam(value = "searchType", required = false) String searchType,
            Model model
    ) {
        model.addAttribute("boardTypes",  boardService.getBoardTypes());
        model.addAttribute("boards",      boardService.getBoards(null, keyword, searchType));
        model.addAttribute("typeCode",    null);
        model.addAttribute("currentType", null);
        model.addAttribute("keyword",     keyword);
        model.addAttribute("searchType",  searchType);
        return "views/board/boardList";
    }

    // /boards/{typeCode} → 특정 게시판 목록
    @GetMapping("/{typeCode}")
    public String list(
            @PathVariable("typeCode")                              String typeCode,
            @RequestParam(value = "keyword",    required = false)  String keyword,
            @RequestParam(value = "searchType", required = false)  String searchType,
            Model model
    ) {
        BoardTypeVO currentType = boardService.getBoardTypeByCode(typeCode);
        if (currentType == null) return "redirect:/boards";

        model.addAttribute("boardTypes",   boardService.getBoardTypes());
        model.addAttribute("boards",       boardService.getBoards(typeCode, keyword, searchType));
        model.addAttribute("typeCode",     typeCode);
        model.addAttribute("currentType",  currentType);
        model.addAttribute("keyword",      keyword);
        model.addAttribute("searchType",   searchType);
        return "views/board/boardList";
    }

    // ── 게시글 상세 ──────────────────────────────────────────
    @GetMapping("/{typeCode}/{boardIdx}")
    public String detail(
            @PathVariable("typeCode")  String typeCode,
            @PathVariable("boardIdx")  Long   boardIdx,
            Model model
    ) {
        BoardVO       board   = boardService.getBoardDetail(boardIdx);
        List<ReplyVO> replies = boardService.getReplies(boardIdx);

        model.addAttribute("board",      board);
        model.addAttribute("replies",    replies);
        model.addAttribute("typeCode",   typeCode);
        model.addAttribute("boardTypes", boardService.getBoardTypes());
        return "views/board/boardDetail";
    }

    // ── 게시글 작성 폼 ───────────────────────────────────────
    @GetMapping("/{typeCode}/new")
    public String newForm(
            @PathVariable("typeCode") String typeCode,
            Model model
    ) {
        BoardTypeVO currentType = boardService.getBoardTypeByCode(typeCode);
        if (currentType == null) return "redirect:/boards";

        model.addAttribute("boardTypes",  boardService.getBoardTypes());
        model.addAttribute("currentType", currentType);
        model.addAttribute("typeCode",    typeCode);
        return "views/board/boardNew";
    }

    // ── 게시글 등록 처리 ─────────────────────────────────────
    @PostMapping("/{typeCode}")
    public String create(
            @PathVariable("typeCode")     String  typeCode,
            @RequestParam("boardTitle")   String  boardTitle,
            @RequestParam("boardContent") String  boardContent,
            HttpServletRequest request,
            HttpSession session
    ) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/members/login";

        BoardTypeVO boardType = boardService.getBoardTypeByCode(typeCode);
        if (boardType == null) return "redirect:/boards";

        BoardVO board = new BoardVO();
        board.setMemIdx(loginUser.getMemIdx());
        board.setBoardTypeIdx(boardType.getBoardTypeIdx());
        board.setBoardTitle(boardTitle);
        board.setBoardContent(boardContent);
        board.setBoardIp(getClientIp(request));

        boardService.writeBoard(board);
        return "redirect:/boards/" + typeCode + "/" + board.getBoardIdx();
    }

    // ── 게시글 수정 폼 ───────────────────────────────────────
    @GetMapping("/{typeCode}/{boardIdx}/edit")
    public String editForm(
            @PathVariable("typeCode") String typeCode,
            @PathVariable("boardIdx") Long   boardIdx,
            Model model, HttpSession session
    ) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/members/login";

        BoardVO board = boardService.getBoardDetail(boardIdx);
        if (!board.getMemIdx().equals(loginUser.getMemIdx())) {
            return "redirect:/boards/" + typeCode + "/" + boardIdx;
        }
        model.addAttribute("board",    board);
        model.addAttribute("typeCode", typeCode);
        return "views/board/boardEdit";
    }

    // ── 게시글 수정 처리 ─────────────────────────────────────
    @PostMapping("/{typeCode}/{boardIdx}/edit")
    public String edit(
            @PathVariable("typeCode") String typeCode,
            @PathVariable("boardIdx") Long   boardIdx,
            @RequestParam("boardTitle")   String boardTitle,
            @RequestParam("boardContent") String boardContent,
            HttpSession session
    ) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/members/login";

        BoardVO board = new BoardVO();
        board.setBoardIdx(boardIdx);
        board.setBoardTitle(boardTitle);
        board.setBoardContent(boardContent);
        boardService.editBoard(board);
        return "redirect:/boards/" + typeCode + "/" + boardIdx;
    }

    // ── 게시글 삭제 ──────────────────────────────────────────
    @PostMapping("/{typeCode}/{boardIdx}/delete")
    public String delete(
            @PathVariable("typeCode") String typeCode,
            @PathVariable("boardIdx") Long   boardIdx,
            HttpSession session
    ) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/members/login";

        boardService.removeBoard(boardIdx);
        return "redirect:/boards/" + typeCode;
    }

    // ── 댓글 등록 ────────────────────────────────────────────
    @PostMapping("/{typeCode}/{boardIdx}/replies")
    public String createReply(
            @PathVariable("typeCode") String typeCode,
            @PathVariable("boardIdx") Long   boardIdx,
            @RequestParam("replyContent") String replyContent,
            @RequestParam(value = "parentReplyIdx", required = false) Long parentReplyIdx,
            HttpServletRequest request,
            HttpSession session
    ) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/members/login";

        ReplyVO reply = new ReplyVO();
        reply.setBoardIdx(boardIdx);
        reply.setMemIdx(loginUser.getMemIdx());
        reply.setReplyContent(replyContent);
        reply.setReplyIp(getClientIp(request));

        boardService.writeReply(reply, parentReplyIdx);
        return "redirect:/boards/" + typeCode + "/" + boardIdx;
    }

    // ── 댓글 삭제 ────────────────────────────────────────────
    @PostMapping("/{typeCode}/replies/{replyIdx}/delete")
    public String deleteReply(
            @PathVariable("typeCode")  String typeCode,
            @PathVariable("replyIdx")  Long   replyIdx,
            @RequestParam("boardIdx")  Long   boardIdx,
            HttpSession session
    ) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/members/login";

        boardService.removeReply(replyIdx);
        return "redirect:/boards/" + typeCode + "/" + boardIdx;
    }

    // ── IP 유틸 ──────────────────────────────────────────────
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
