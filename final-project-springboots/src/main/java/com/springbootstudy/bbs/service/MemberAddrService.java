package com.springbootstudy.bbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.domain.MemberAddrVO;
import com.springbootstudy.bbs.mapper.MemberAddrMapper;

@Service
public class MemberAddrService {

	@Autowired
    private MemberAddrMapper memberAddrMapper;

    // 주소 저장
    public int registerAddr(MemberAddrVO vo) {
        return memberAddrMapper.insertAddr(vo);
    }

    // 주소 리스트 조회
    public List<MemberAddrVO> selectAddrList(Long memIdx) {
        return memberAddrMapper.selectAddrList(memIdx);
    }
 

}
