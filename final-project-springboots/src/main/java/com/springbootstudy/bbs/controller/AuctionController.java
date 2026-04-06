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

import com.springbootstudy.bbs.domain.AuctionListDTO;
import com.springbootstudy.bbs.domain.BidListDTO;
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
	
	// 구매 요청 전체 리스트 조회
	@GetMapping({"/auctionList"})
    public String auctionList(Model model) { // 쟁반(Model)을 준비
        
		// 페이지 열릴 때마다 마감된 경매 상태 자동 업데이트
	    auctionService.updateExpiredAuctions();
		
        // 서비스한테서 리스트를 가져옴
        List<AuctionListDTO> list = auctionService.AuctionList();
        
        // 쟁반에 리스트를 올리고 'auctionList'라는 이름표를 붙임
        model.addAttribute("auctionList", list);
        
        // "views/auction/auctionList.html" 화면으로 쟁반을 들고 이동
        return "views/auction/auctionList"; 
    }
	
	// 구매 요청 글 상세페이지(입찰 제안 등 포함)
	@GetMapping("/auction/auctionDetail/{auctionIdx}")
	public String auctionDetail(@PathVariable("auctionIdx") Long auctionIdx, Model model) {
	    
		// 상세 페이지 열릴 때도 상태 업데이트
	    auctionService.updateExpiredAuctions();
		
	    // 서비스 호출
	    AuctionListDTO detail = auctionService.auctionDetail(auctionIdx);
	    
	    // 만약 없는 글 번호라면? (예외 처리)
	    if (detail == null) {
	        return "redirect:/auction/list"; // 다시 리스트로
	    }
	    
	    // 모델에 데이터 담기
	    model.addAttribute("detail", detail);
	    
	    
	    // ======================================================================
	    
	    // bid 관련
	    
        // 해당 글의 입찰 리스트 가져오기
        List<BidListDTO> bidList = bidService.BidList(auctionIdx);
        
        model.addAttribute("detail", detail);
        model.addAttribute("bidList", bidList); // HTML로 던지기
	    
	    // 상세보기 화면으로 이동
	    return "views/auction/auctionDetail"; 
	}
	
	// 등록 폼으로 이동 (GET)
    @GetMapping("/auction/register")
    public String registerForm(HttpSession session) {
        // 세션 이름 'loginUser'로 통일! 
        if (session.getAttribute("loginUser") == null) {
            log.info("로그인 정보 없음 -> 로그인 페이지로 리다이렉트");
            return "redirect:/login"; 
        }
        return "views/auction/auctionRegister";
    }

 // 등록 실행 (POST) - 파일 업로드 추가
    @PostMapping("/auction/register")
    public String registerAction(AuctionListDTO dto,
                                  @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        dto.setBuyerIdx(loginUser.getMemIdx());

        // 파일 업로드 처리
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            try {
                // 저장할 폴더 경로 (프로젝트 내 static/uploads/)
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs(); // 폴더 없으면 생성

                // UUID로 파일명 중복 방지
                String fileName = UUID.randomUUID().toString() + "_" + thumbnailFile.getOriginalFilename();
                thumbnailFile.transferTo(new File(uploadDir + fileName));

                // DB에는 URL 경로로 저장
                dto.setItemThumbnailImg("/uploads/" + fileName);
            } catch (Exception e) {
                log.error("이미지 업로드 실패", e);
                dto.setItemThumbnailImg(null);
            }
        } else {
            dto.setItemThumbnailImg(null);
        }

        log.info("경매 등록 시도: {}", dto);

        try {
            auctionService.registerAuction(dto);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auction/register?error";
        } catch (Exception e) {
            log.error("경매 등록 중 에러 발생", e);
            ra.addFlashAttribute("errorMessage", "등록 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/auction/register?error";
        }

        return "redirect:/auctionList";
    }

    // 입찰 등록
    @PostMapping("/auction/bid")
    public String registerBid(BidListDTO bidDto,
                               @RequestParam(value = "bidImageFile", required = false) MultipartFile bidImageFile,
                               HttpSession session,
                               RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        bidDto.setBidderIdx(loginUser.getMemIdx());

        // 경매 정보 조회
        AuctionListDTO auction = auctionService.auctionDetail(bidDto.getAuctionIdx());
        if (auction == null) {
            ra.addFlashAttribute("bidError", "존재하지 않는 경매입니다.");
            return "redirect:/auctionList";
        }

        // 본인 경매 입찰 방지 - null 안전하게 Objects.equals 사용
        if (java.util.Objects.equals(auction.getBuyerIdx(), loginUser.getMemIdx())) {
            ra.addFlashAttribute("bidError", "본인이 등록한 경매에는 입찰할 수 없습니다.");
            return "redirect:/auction/auctionDetail/" + bidDto.getAuctionIdx();
        }

        // 실제 경매에서 itemIdx 가져오기
        bidDto.setItemIdx(auction.getItemIdx());

        // 입찰 이미지 업로드 처리
        if (bidImageFile != null && !bidImageFile.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String fileName = UUID.randomUUID().toString() + "_" + bidImageFile.getOriginalFilename();
                bidImageFile.transferTo(new File(uploadDir + fileName));
                bidDto.setItemThumbnailImg("/uploads/" + fileName);
            } catch (Exception e) {
                log.error("입찰 이미지 업로드 실패", e);
            }
        }

        try {
            bidService.registerBid(bidDto);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("bidError", e.getMessage());
            return "redirect:/auction/auctionDetail/" + bidDto.getAuctionIdx();
        }

        return "redirect:/auction/auctionDetail/" + bidDto.getAuctionIdx();
    }
    
    // 경매 삭제 (소프트 딜리트)
    @PostMapping("/auction/delete/{auctionIdx}")
    public String deleteAuction(@PathVariable("auctionIdx") Long auctionIdx,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        try {
            auctionService.deleteAuction(auctionIdx, loginUser.getMemIdx());
            ra.addFlashAttribute("successMessage", "구매요청이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/auctionList";
    }

    // 입찰 삭제 (소프트 딜리트)
    @PostMapping("/auction/bid/delete/{bidIdx}")
    public String deleteBid(@PathVariable("bidIdx") Long bidIdx,
                             @RequestParam("auctionIdx") Long auctionIdx,
                             HttpSession session,
                             RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        try {
            bidService.deleteBid(bidIdx, loginUser.getMemIdx());
            ra.addFlashAttribute("successMessage", "입찰이 취소되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("bidError", e.getMessage());
        }

        return "redirect:/auction/auctionDetail/" + auctionIdx;
    }
    
    // 경매 수정 폼 (GET)
    @GetMapping("/auction/edit/{auctionIdx}")
    public String editAuctionForm(@PathVariable("auctionIdx") Long auctionIdx,
                                   HttpSession session, Model model) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        AuctionListDTO detail = auctionService.auctionDetail(auctionIdx);
        if (detail == null || !detail.getBuyerIdx().equals(loginUser.getMemIdx())) {
            return "redirect:/auctionList";
        }

        model.addAttribute("auction", detail);
        return "views/auction/auctionEdit";
    }

    // 경매 수정 실행 (POST)
    @PostMapping("/auction/edit/{auctionIdx}")
    public String editAuction(@PathVariable("auctionIdx") Long auctionIdx,
                               AuctionListDTO dto,
                               HttpSession session,
                               RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        dto.setAuctionIdx(auctionIdx);
        dto.setBuyerIdx(loginUser.getMemIdx());

        try {
            auctionService.updateAuction(dto);
            ra.addFlashAttribute("successMessage", "구매요청이 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auction/edit/" + auctionIdx;
        }

        return "redirect:/auction/auctionDetail/" + auctionIdx;
    }

    // 입찰 상세 (GET) - 구매자만 접근 가능
    @GetMapping("/auction/bid/detail/{bidIdx}")
    public String bidDetail(@PathVariable("bidIdx") Long bidIdx,
                             HttpSession session, Model model) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        BidListDTO bid = bidService.findBidById(bidIdx);
        if (bid == null) return "redirect:/auctionList";

        // 해당 경매의 구매자인지 확인
        AuctionListDTO auction = auctionService.auctionDetail(bid.getAuctionIdx());
        if (auction == null || !auction.getBuyerIdx().equals(loginUser.getMemIdx())) {
            return "redirect:/auction/auctionDetail/" + bid.getAuctionIdx();
        }

        model.addAttribute("bid", bid);
        model.addAttribute("auction", auction);
        return "views/bid/bidDetail";
    }

    // 입찰 수정 폼 (GET)
    @GetMapping("/auction/bid/edit/{bidIdx}")
    public String editBidForm(@PathVariable("bidIdx") Long bidIdx,
                               HttpSession session, Model model) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        BidListDTO bid = bidService.findBidById(bidIdx);
        if (bid == null || !bid.getBidderIdx().equals(loginUser.getMemIdx())) {
            return "redirect:/auctionList";
        }

        model.addAttribute("bid", bid);
        return "views/auction/bidEdit";
    }

    // 입찰 수정 실행 (POST)
    @PostMapping("/auction/bid/edit/{bidIdx}")
    public String editBid(@PathVariable("bidIdx") Long bidIdx,
                           BidListDTO bidDto,
                           HttpSession session,
                           RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        bidDto.setBidIdx(bidIdx);
        bidDto.setBidderIdx(loginUser.getMemIdx());

        try {
            bidService.updateBid(bidDto);
            ra.addFlashAttribute("successMessage", "입찰이 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auction/bid/edit/" + bidIdx;
        }

        return "redirect:/auction/auctionDetail/" + bidDto.getAuctionIdx();
    }

    // 낙찰 처리 (POST) - 구매자만 가능
    @PostMapping("/auction/bid/win/{bidIdx}")
    public String selectWinner(@PathVariable("bidIdx") Long bidIdx,
                                 @RequestParam("auctionIdx") Long auctionIdx,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // 해당 경매의 구매자인지 확인
        AuctionListDTO auction = auctionService.auctionDetail(auctionIdx);
        if (auction == null || !auction.getBuyerIdx().equals(loginUser.getMemIdx())) {
            ra.addFlashAttribute("bidError", "권한이 없습니다.");
            return "redirect:/auction/auctionDetail/" + auctionIdx;
        }

        try {
            bidService.selectWinner(bidIdx, auctionIdx);
            // 경매 상태도 마감(2)으로 변경
            auctionService.updateExpiredAuctions();
            ra.addFlashAttribute("successMessage", "낙찰 처리가 완료되었습니다!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("bidError", e.getMessage());
        }

        return "redirect:/auction/auctionDetail/" + auctionIdx;
    }
    
}
