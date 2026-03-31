package com.springbootstudy.bbs.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.springbootstudy.bbs.domain.MemberVO;

@Mapper
public interface MemberProfileMapper {

	

	// 회원정보 수정
	void update(MemberVO vo);
	MemberVO selectOneFromId(String memId); 
	String selectGradeNameByMemId(String memId);	// grade만 따로 조회
	
	
	
	


}
