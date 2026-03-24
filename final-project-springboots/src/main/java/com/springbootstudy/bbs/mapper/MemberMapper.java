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
	
	int countByMemId(@Param("memId") String memId);

	
	// 로그인 - 로그인 처리용
	int loginMember(
            @Param("memId") String memId,
            @Param("memPwd") String memPwd
    );

	MemberVO getMember(String memId);



}
