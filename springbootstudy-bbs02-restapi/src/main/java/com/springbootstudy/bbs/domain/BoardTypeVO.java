package com.springbootstudy.bbs.domain;

import lombok.Data;

@Data
public class BoardTypeVO {
    private Integer boardTypeIdx;      // PK
    private String  boardTypeCode;     // 예: "GOLF_BOARD", "SKI_BOARD"
    private String  boardTypeName;     // 예: "골프 게시판"
    private String  boardCanComment;   // 'Y' / 'N'
    private Integer boardMinRole;      // 최소 권한 (role_idx)
}

