package com.springbootstudy.bbs.domain;

import java.util.Date;

import lombok.Data;

@Data
public class ReplyVO {
    private Long   replyIdx;        // PK
    private Long   boardIdx;        // FK → board.board_idx
    private Long   memIdx;          // FK → member.mem_idx
    private String replyContent;
    private String replyIp;
    private Date   replyRegdate;
    private Date   replyModdate;
    private String replyIsDeleted;  // 'Y' / 'N'
    private Date   replyDeldate;
}

