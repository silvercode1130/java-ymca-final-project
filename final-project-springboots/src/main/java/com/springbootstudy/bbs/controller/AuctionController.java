package com.springbootstudy.bbs.controller;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springbootstudy.bbs.domain.AuctionDTO;
import com.springbootstudy.bbs.domain.BidDTO;
import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.service.AuctionService;
import com.springbootstudy.bbs.service.BidService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private BidService bidService;

    // 경매 목록 (/auctions)
    @GetMapping("/auctions")
    public String auctionList(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        auctionService.updateExpiredAuctions();
        List<AuctionDTO> list = auctionService.AuctionList(null, keyword);

        model.addAttribute("auctionList", list);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", null);
        return "views/auction/auctionList";
    }
    
    // 카테고리 필터 (/auctions/category/{categoryCode})
    @GetMapping("/auctions/category/{categoryCode}")
    public String auctionListByCategory(
            @PathVariable("categoryCode") String categoryCode,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        auctionService.updateExpiredAuctions();
        List<AuctionDTO> list = auctionService.AuctionList(categoryCode, keyword);
        
        System.out.println("전달된 코드: " + categoryCode);
        
        model.addAttribute("auctionList", list);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", categoryCode);
        return "views/auction/auctionList";
    }

    // 경매 상세 조회 (입찰 리스트 포함) (/auctions/{auctionIdx})
    @GetMapping("/auctions/{auctionIdx}")
    public String auctionDetail(
            @PathVariable("auctionIdx") Long auctionIdx,
            Model model) {

        auctionService.updateExpiredAuctions();

        AuctionDTO detail = auctionService.auctionDetail(auctionIdx);
        if (detail == null) return "redirect:/auctions";

        List<BidDTO> bidList = bidService.BidList(auctionIdx);

        model.addAttribute("detail", detail);
        model.addAttribute("bidList", bidList);
        model.addAttribute("mode", "list");   // 기본: 입찰 목록 표시
        return "views/auction/auctionDetail";
    }

    // 경매 등록 폼 이동 (/auctions/new)
    @GetMapping("/auctions/new")
    public String registerForm(HttpSession session) {
    	
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/views/member/login";
        }
        
        return "views/auction/auctionRegister";
    }

    // 경매 등록 실행 (POST /auctions)
    @PostMapping("/auctions")
    public String registerAction(AuctionDTO dto,
                                  @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/views/member/login";
        
        // DTO에 로그인한 사용자의 고유 번호(buyerIdx) 세팅
        dto.setBuyerIdx(loginUser.getMemIdx());
        
        // 파일 업로드 처리 (이미지가 있을 경우에만 실행)
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            try {
            	// 프로젝트 내부의 static/uploads 폴더 경로 설정 (개발 환경용)
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";
                File dir = new File(uploadDir);
                
                // 폴더가 없으면 생성
                if (!dir.exists()) dir.mkdirs();
                
                // 파일명 중복 방지를 위해 UUID 사용
                String fileName = UUID.randomUUID() + "_" + thumbnailFile.getOriginalFilename();
                thumbnailFile.transferTo(new File(uploadDir + fileName));
                
                // DB에는 웹에서 접근 가능한 경로("/uploads/파일명")로 저장
                dto.setAuctionThumbnailImg("/uploads/" + fileName);
            } catch (Exception e) {
                log.error("이미지 업로드 실패", e);
                dto.setAuctionThumbnailImg(null);  // 실패 시 null 처리 (또는 기본이미지)
            }
        } else {
            dto.setAuctionThumbnailImg(null);  // 첨부 파일이 없는 경우
        }
        
        // 서비스 호출 및 예외 처리
        try {
            auctionService.registerAuction(dto);
        } catch (IllegalArgumentException e) {
        	
        	// 서비스 계층에서 던진 검증 에러 메시지를 화면으로 전달
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auctions/new";
            
        } catch (Exception e) {
            log.error("경매 등록 에러", e);
            ra.addFlashAttribute("errorMessage", "등록 중 오류가 발생했습니다.");
            return "redirect:/auctions/new";
        }
        return "redirect:/auctions";
    }

    // 경매 취소 (/auctions/{auctionIdx}/delete)
    @PostMapping("/auctions/{auctionIdx}/delete")
    public String deleteAuction(@PathVariable("auctionIdx") Long auctionIdx,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/views/member/login";

        try {
        	// 작성자 본인 확인은 서비스 계층에서 수행 (안전함)
            auctionService.deleteAuction(auctionIdx, loginUser.getMemIdx());
            ra.addFlashAttribute("successMessage", "구매요청이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/auctions";
    }
    
    // 관리자 경매 삭제 (/auctions/{auctionIdx}/admin-delete)
    @PostMapping("/auctions/{auctionIdx}/admin-delete")
    public String adminDeleteAuction(@PathVariable("auctionIdx") Long auctionIdx,
                                      HttpSession session,
                                      RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/views/member/login";

        // 관리자(memRoleIdx == 2)만 접근 가능
        if (loginUser.getMemRoleIdx() == null || loginUser.getMemRoleIdx() != 2) {
            ra.addFlashAttribute("errorMessage", "관리자만 사용할 수 있는 기능입니다.");
            return "redirect:/auctions/" + auctionIdx;
        }

        try {
            auctionService.adminDeleteAuction(auctionIdx);
            ra.addFlashAttribute("successMessage", "관리자 권한으로 경매가 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/auctions";
    }

    // 경매 수동 마감 (/auctions/{auctionIdx}/close)
    @PostMapping("/auctions/{auctionIdx}/close")
    public String closeAuction(@PathVariable("auctionIdx") Long auctionIdx,
                                HttpSession session,
                                RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/views/member/login";

        AuctionDTO detail = auctionService.auctionDetail(auctionIdx);
        if (detail == null || !detail.getBuyerIdx().equals(loginUser.getMemIdx())) {
            ra.addFlashAttribute("errorMessage", "권한이 없습니다.");
            return "redirect:/auctions/" + auctionIdx;
        }

        try {
            auctionService.closeAuction(auctionIdx, loginUser.getMemIdx());
            ra.addFlashAttribute("successMessage", "경매가 마감되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/auctions/" + auctionIdx;
    }

    // 유찰 처리 (/auctions/{auctionIdx}/fail)
    @PostMapping("/auctions/{auctionIdx}/fail")
    public String failAuction(@PathVariable("auctionIdx") Long auctionIdx,
                               HttpSession session,
                               RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/views/member/login";

        AuctionDTO detail = auctionService.auctionDetail(auctionIdx);
        if (detail == null || !detail.getBuyerIdx().equals(loginUser.getMemIdx())) {
            ra.addFlashAttribute("errorMessage", "권한이 없습니다.");
            return "redirect:/auctions/" + auctionIdx;
        }

        auctionService.updateAuctionStatus(auctionIdx, 3);
        ra.addFlashAttribute("successMessage", "유찰 처리되었습니다.");
        return "redirect:/auctions/" + auctionIdx;
    }

    // 입찰 폼 페이지 (/auctions/{auctionIdx}/bids GET)
    @GetMapping("/auctions/{auctionIdx}/bids")
    public String bidRegisterForm(
            @PathVariable("auctionIdx") Long auctionIdx,
            HttpSession session, Model model,
            RedirectAttributes ra) {

        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/views/member/login";

        AuctionDTO detail = auctionService.auctionDetail(auctionIdx);
        if (detail == null) return "redirect:/auctions";

        // 구매자 본인은 입찰 불가 → 상세로 리다이렉트
        if (detail.getBuyerIdx().equals(loginUser.getMemIdx())) {
            ra.addFlashAttribute("bidError", "본인이 등록한 경매에는 입찰할 수 없습니다.");
            return "redirect:/auctions/" + auctionIdx;
        }

        // 진행중(1)이 아니면 입찰 불가
        if (detail.getAuctionStatusIdx() != 1) {
            ra.addFlashAttribute("bidError", "진행중인 경매에만 입찰할 수 있습니다.");
            return "redirect:/auctions/" + auctionIdx;
        }

        List<BidDTO> bidList = bidService.BidList(auctionIdx);

        model.addAttribute("detail", detail);
        model.addAttribute("bidList", bidList);
        model.addAttribute("mode", "bidForm");   // 오른쪽 패널: 입찰 폼
        return "views/auction/auctionDetail";
    }

    // 입찰 등록 (/auctions/{auctionIdx}/bids POST)
    @PostMapping("/auctions/{auctionIdx}/bids")
    public String registerBid(@PathVariable("auctionIdx") Long auctionIdx,
    						   BidDTO bidDto,
                               @RequestParam(value = "bidImageFile", required = false) MultipartFile bidImageFile,
                               HttpSession session,
                               RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/views/member/login";
        
        // 기본 데이터 세팅
        bidDto.setAuctionIdx(auctionIdx);
        bidDto.setBidderIdx(loginUser.getMemIdx());
        
        // 경매 정보 조회 (검증용)
        AuctionDTO auction = auctionService.auctionDetail(auctionIdx);
        if (auction == null) return "redirect:/auctions";

        // 본인 경매 입찰 방지
        if (java.util.Objects.equals(auction.getBuyerIdx(), loginUser.getMemIdx())) {
            ra.addFlashAttribute("bidError", "본인이 등록한 경매에는 입찰할 수 없습니다.");
            return "redirect:/auctions/" + auctionIdx;
        }

        // 희망 최대가 초과 방지
        if (bidDto.getBidPrice() != null && auction.getAuctionTargetPrice() != null
                && bidDto.getBidPrice() > auction.getAuctionTargetPrice()) {
            ra.addFlashAttribute("bidError", "구매자의 희망가보다 높은 금액은 제안할 수 없습니다.");
            return "redirect:/auctions/" + auctionIdx;
        }
        
        // 이미지 업로드 처리
        if (bidImageFile != null && !bidImageFile.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();
                String fileName = UUID.randomUUID() + "_" + bidImageFile.getOriginalFilename();
                bidImageFile.transferTo(new File(uploadDir + fileName));
                bidDto.setItemThumbnailImg("/uploads/" + fileName);
            } catch (Exception e) {
                log.error("입찰 이미지 업로드 실패", e);
            }
        }
        
        // itemCategoryIdx는 경매에서 자동 세팅
        bidDto.setItemCategoryIdx(auction.getItemCategoryIdx());
        
        // 서비스 호출
        try {
            bidService.registerBid(bidDto);
            ra.addFlashAttribute("successMessage", "입찰 제안이 등록되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("bidError", e.getMessage());
            return "redirect:/auctions/" + auctionIdx;
        }
        
        return "redirect:/auctions/" + auctionIdx;
    }

    // 입찰 상세 (/bids/{bidIdx})
    @GetMapping("/auctions/{auctionIdx}/bids/{bidIdx}")
    public String bidDetailPanel(
            @PathVariable("auctionIdx") Long auctionIdx,
            @PathVariable("bidIdx") Long bidIdx,
            HttpSession session, Model model,
            RedirectAttributes ra) {

        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/views/member/login";

        AuctionDTO detail = auctionService.auctionDetail(auctionIdx);
        if (detail == null) return "redirect:/auctions";

        // 구매자만 입찰 상세 열람 가능
        if (!detail.getBuyerIdx().equals(loginUser.getMemIdx())) {
            ra.addFlashAttribute("bidError", "구매자만 입찰 상세를 열람할 수 있습니다.");
            return "redirect:/auctions/" + auctionIdx;
        }

        BidDTO selectedBid = bidService.findBidById(bidIdx);
        if (selectedBid == null) return "redirect:/auctions/" + auctionIdx;

        List<BidDTO> bidList = bidService.BidList(auctionIdx);

        model.addAttribute("detail", detail);
        model.addAttribute("bidList", bidList);
        model.addAttribute("selectedBid", selectedBid);
        model.addAttribute("mode", "bidDetail");   // 오른쪽 패널: 입찰 상세
        return "views/auction/auctionDetail";
    }

    // 입찰 취소 (/bids/{bidIdx}/cancel)
    @PostMapping("/bids/{bidIdx}/cancel")
    public String cancelBid(@PathVariable("bidIdx") Long bidIdx,
                             @RequestParam("auctionIdx") Long auctionIdx,
                             HttpSession session,
                             RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/views/member/login";

        try {
            bidService.deleteBid(bidIdx, loginUser.getMemIdx());
            ra.addFlashAttribute("successMessage", "입찰이 취소되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("bidError", e.getMessage());
        }
        return "redirect:/auctions/" + auctionIdx;
    }
    
    // 관리자 입찰 삭제 (/bids/{bidIdx}/admin-cancel)
    @PostMapping("/bids/{bidIdx}/admin-cancel")
    public String adminDeleteBid(@PathVariable("bidIdx") Long bidIdx,
                                  @RequestParam("auctionIdx") Long auctionIdx,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/views/member/login";

        if (loginUser.getMemRoleIdx() == null || loginUser.getMemRoleIdx() != 2) {
            ra.addFlashAttribute("errorMessage", "관리자만 사용할 수 있는 기능입니다.");
            return "redirect:/auctions/" + auctionIdx;
        }

        try {
            bidService.adminDeleteBid(bidIdx);
            ra.addFlashAttribute("successMessage", "관리자 권한으로 입찰이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("bidError", e.getMessage());
        }
        return "redirect:/auctions/" + auctionIdx;
    }
    
    // 낙찰 처리 (/auctions/{auctionIdx}/bids/{bidIdx}/win)
    @PostMapping("/auctions/{auctionIdx}/bids/{bidIdx}/win")
    public String selectWinner(@PathVariable("auctionIdx") Long auctionIdx,
                                 @PathVariable("bidIdx") Long bidIdx,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/views/member/login";

        AuctionDTO auction = auctionService.auctionDetail(auctionIdx);
        if (auction == null || !auction.getBuyerIdx().equals(loginUser.getMemIdx())) {
            ra.addFlashAttribute("bidError", "권한이 없습니다.");
            return "redirect:/auctions/" + auctionIdx;
        }

        try {
            bidService.selectWinner(bidIdx, auctionIdx);
            ra.addFlashAttribute("successMessage", "낙찰 처리가 완료되었습니다!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("bidError", e.getMessage());
        }
        return "redirect:/auctions/" + auctionIdx;
    }
}