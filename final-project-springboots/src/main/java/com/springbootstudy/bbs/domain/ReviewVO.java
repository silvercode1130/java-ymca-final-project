package com.springbootstudy.bbs.domain;

import java.time.LocalDateTime;

import lombok.Data;

// 임시 VO 입니다
// DB 확정 전이라 사라질 수 있어요!

@Data
public class ReviewVO {
	private Long reviewIdx;			// PK
	private Long reviewWriterIdx;	// 리뷰 작성자
	private Long reviewTargetIdx;	// 리뷰 대상자
	private Long auctionIdx;		// 역경매
	
	private String reviewTitle;		// 리뷰 제목
	private String reviewContent;	// 리뷰 내용
	private Integer  reviewStar;	// 리뷰 별점 (1 ~ 5점 까지 가능)
	
	private LocalDateTime reviewRegdate;	// 리뷰 작성일
	private String reviewIsDeleted;			// 리뷰 삭제 여부 default N 	--> Y = 탈퇴 x / N = 탈퇴 o
	private LocalDateTime reviewDeldate;	// 리뷰 삭제일
	
}
