package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.springbootstudy.bbs.domain.MemberAddrVO;

@Mapper
public interface MemberAddrMapper {

    // 주소 등록
    int insertAddr(MemberAddrVO memberAddrVO);
	List<MemberAddrVO> selectAddrList(Long memIdx);
	
	// 대표 주소 하나만 등록하기
	void resetPrimaryAddr(Long memIdx); 

}
