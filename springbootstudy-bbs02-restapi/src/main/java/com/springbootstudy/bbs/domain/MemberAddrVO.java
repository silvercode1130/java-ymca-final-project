package com.springbootstudy.bbs.domain;

import lombok.Data;

@Data
public class MemberAddrVO {
    private Long   addrIdx;        // PK
    private Long   memIdx;         // FK → member.mem_idx
    private String memZipcode;
    private String memAddr;
    private String memAddrDetail;
    private String isPrimary;      // 'Y' / 'N' 대표 주소 여부
}

