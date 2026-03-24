package com.springbootstudy.bbs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.domain.MemberVO;
import com.springbootstudy.bbs.mapper.MemberMapper;

@Service
public class MemberService {

	@Autowired
    private MemberMapper memberMapper;

	// 회원가입 처리
    public void insertMember(
            String memId,
            String memPwd,
            String memName,
            String memTel,
            String memEmail,
            String memIp,
            Long memRoleIdx,
            int memGradeIdx
    ) {

        memberMapper.insertMember(
                memId, memPwd, memName, memTel, memEmail,
                memIp, memRoleIdx, memGradeIdx
        );
    }
	
	// 회원 가입시 아이디 중복을 체크하는 메서드	
	public boolean overlapIdCheck(String memId) {
		MemberVO member = memberMapper.getMember(memId);
		
		if(member == null) {
			return false; 
		}  
		return true; 
	}

	
    // 로그인 처리
    public int loginMember(String memId, String memPwd) {
        return memberMapper.loginMember(memId, memPwd);
    }


}
