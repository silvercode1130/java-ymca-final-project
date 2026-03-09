package com.springbootstudy.bbs.domain;

import lombok.Data;

@Data
public class MemberProfileVO {
    private Long   memIdx;       // PK & FK → member.mem_idx
    private String memNickname;
    private String memIntro;
    private String memImg;
}
