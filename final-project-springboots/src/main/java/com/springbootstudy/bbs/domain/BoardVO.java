package com.springbootstudy.bbs.domain;

import java.time.LocalDateTime;

import lombok.Data;

//게시글
@Data
public class BoardVO {

 private Long    		boardIdx;        // PK
 private Long    		memIdx;          // FK → member.mem_idx
 private String  		boardTitle;      // 제목
 private String  		boardContent;    // 내용
 private String  		boardIp;         // IP
 private String  		boardThumbnail;  // 썸네일
 private Long    		boardViewCount;  // 조회수
 private Integer 		boardTypeIdx;    // FK → board_type.board_type_idx
 private LocalDateTime 	boardRegdate;	 // 등록일
 private LocalDateTime 	boardModdate;	 // 수정일
 private String  		boardIsDeleted;  // 'Y' / 'N'
 private LocalDateTime 	boardDeldate; 	 // 삭제일

}


