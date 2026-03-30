package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.springbootstudy.bbs.domain.MemberAddrVO;


@Mapper
public interface MemberAddrMapper {

	// 주소 저장
    int insertAddr(MemberAddrVO vo);

    // 주소 목록 조회
    List<MemberAddrVO> selectAddrList(Long memIdx);

}
