package com.springbootstudy.bbs.domain;

import java.time.LocalDateTime;

import lombok.Data;

//댓글
@Data
public class ReplyVO {

 private Long    		replyIdx;        // PK
 private Long    		boardIdx;        // FK → board.board_idx
 private Long    		memIdx;          // FK → member.mem_idx
 private String  		replyContent;    // 댓글 내용
 private String  		replyIp;         // IP
 private LocalDateTime 	replyRegdate;  	 // 등록일
 private LocalDateTime 	replyModdate;  	 // 수정일
 private String  		replyIsDeleted;  // 'Y' / 'N'
 private LocalDateTime 	replyDeldate;  	 // 삭제일
 private Integer 		reply_ref;       // 원댓
 private Integer 		reply_step;      // 댓글 순서
 private Integer 		reply_depth;     // 댓글 깊이

}


