package com.springbootstudy.bbs.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.springbootstudy.bbs.domain.MemberVO;

@Mapper
public interface MemberMapper {
	
	// 회원가입 - 회원가입 처리용
	int insertMember(
            @Param("memId") String memId,
            @Param("memPwd") String memPwd,
            @Param("memName") String memName,
            @Param("memTel") String memTel,
            @Param("memEmail") String memEmail,
	        @Param("memIp") String memIp, 
	        @Param("memRoleIdx") Long memRoleIdx,
	        @Param("memGradeIdx") int memGradeIdx
    );
	
	Long findDefaultRoleIdx(); 
	
	// 회원 조회 
	int countByMemId(@Param("memId") String memId);

	
	
	// 로그인 =============================================
	
    // 아이디 존재 여부 체크
    int checkId(String memId);

    // 비밀번호 조회
    String getPassword(String memId);

    // 회원 정보 조회
    MemberVO getMemberVO(String memId);
    
    // 탈퇴
    int deleteMember(String memId);


}
