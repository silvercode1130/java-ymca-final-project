package com.springbootstudy.bbs.domain;

import lombok.Data;

//회원 등급 코드 테이블
@Data
public class GradeVO { 

 private Integer gradeIdx;     // PK
 private String  gradeName;    // 등급명 (basic / silver / gold / vip)
 private Double  gradeCredit;  // 신용도 기준 

}
