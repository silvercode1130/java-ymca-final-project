package com.springbootstudy.bbs.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.springbootstudy.bbs.domain.MemberProfileVO;

@Mapper
public interface MemberProfileMapper {

    // 조회용
    MemberProfileVO selectProfileByMemIdx(Long memIdx);

    // 저장용
    void insertProfile(MemberProfileVO vo);

    // 수정용
    void updateProfile(MemberProfileVO vo);

    // 닉네임 중복 체크
    int countByNickname(String memNickname);

}
