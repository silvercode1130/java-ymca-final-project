package com.springbootstudy.bbs.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/* DTO(Data Transfer Object) 객체는 요청과 응답 사이에서 계층 간에 값을
 * 전달하는 목적으로 사용하는 객체이다. 요청이 들어올 때와 응답으로 나갈 때
 * 각각 용도에 맞게 정의하여 사용해야 하지만 우리는 게시판 관련 요청과 응답
 * 모두에서 Board 클래스 하나를 정의해 사용할 것이다.
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardVO {
    private Long   boardIdx;        // PK
    private Long   memIdx;          // FK → member.mem_idx
    private String boardTitle;
    private String boardContent;
    private String boardIp;
    private String boardThumbnail;
    private Long   boardReadhit;
    private Integer boardTypeIdx;   // FK → board_type.board_type_idx
    private Date   boardRegdate;
    private Date   boardModdate;
    private String boardIsDeleted;  // 'Y' / 'N'
    private Date   boardDeldate;
}


