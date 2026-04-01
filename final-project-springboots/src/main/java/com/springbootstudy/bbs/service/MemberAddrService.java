package com.springbootstudy.bbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springbootstudy.bbs.domain.MemberAddrVO;
import com.springbootstudy.bbs.mapper.MemberAddrMapper;

@Service
public class MemberAddrService {

	@Autowired
    private MemberAddrMapper memberAddrMapper;

    // 주소 저장
	@Transactional // 필수인 @는 아니지만 안정성을 위해 추가해둘게요!!
    public int registerAddr(MemberAddrVO vo) {
    	
        // 대표주소(Y) 체크했으면 이전 대표주소는 N 처리
        if ("Y".equals(vo.getIsPrimary())) {
            memberAddrMapper.resetPrimaryAddr(vo.getMemIdx());
        }
        return memberAddrMapper.insertAddr(vo); 
    }

    // 주소 리스트 조회
    public List<MemberAddrVO> selectAddrList(Long memIdx) {
        return memberAddrMapper.selectAddrList(memIdx);
    }
    
    
    // 주소 삭제
    public void deleteAddr(Long addrIdx) {
        memberAddrMapper.deleteAddr(addrIdx);
    }
 

}
