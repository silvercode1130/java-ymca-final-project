package com.springbootstudy.bbs.domain;

import java.util.Date;

import lombok.Data;

@Data
public class MemberVO {
    private Long   memIdx;        // PK
    private String memId;         // 로그인 ID
    private String memPwd;        // 비밀번호 해시
    private String memName;
    private String memTel;
    private String memEmail;
    private String memIp;
    private Integer memRoleIdx;   // FK → role.role_idx
    private Integer memGradeIdx;  // FK → grade.grade_idx (크레딧 등급)
    private Date   memBday;
    private Date   memRegdate;
    private String memIsDeleted;  // 'Y' / 'N'
    private Date   memDeldate;
}

