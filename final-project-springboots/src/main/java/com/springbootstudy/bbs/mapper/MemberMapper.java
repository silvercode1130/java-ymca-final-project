package com.springbootstudy.bbs.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

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

	//int insertMember(String memId, String memPwd, String memName, String memTel, String memEmail);
	
	// 회원가입 - 아이디 중복 확인용 
	//int countByMemId(String memId);

	// 회원가입 - 아이디 중복 확인용
	static boolean isDuplicate(String emp_id) {
		// TODO Auto-generated method stub
		return false;
	}
	
	int countByMemId(@Param("memId") String memId);

	
	// 로그인 - 로그인 처리용
	int loginMember(
            @Param("memId") String memId,
            @Param("memPwd") String memPwd
    );
	//int loginMember(String memId, String memPwd);



}
