package com.springbootstudy.bbs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.mapper.MemberMapper;

@Service
public class MemberService {

	@Autowired
    private MemberMapper memberMapper;

	// Caused by: org.apache.ibatis.binding.BindingException: Parameter 'memIp' not found. Available parameters are
	// [memEmail, param5, memTel, memName, memPwd, param3, param4, param1, memId, param2]
	// 넘어감 - memId memPwd memName memTel memEmail
	// 안넘어감 - memIp  memRoleIdx memGradeIdx 안넘어간건 3개인데 param은 왜 5가지지? 
    // 회원가입 처리
	
	public int insertMember(String memId, String memPwd, String memName, 
	            String memTel, String memEmail,
	            String memIp) {
	
		Long memRoleIdx = memberMapper.findDefaultRoleIdx();
		int memGradeIdx = 1;
		
		return memberMapper.insertMember(
		memId, memPwd, memName, memTel, memEmail,
		memIp, memRoleIdx, memGradeIdx
		);
	} 
	
//    public int insertMember(String memId, String memPwd, String memName, 
//    				String memTel, String memEmail, String memIp, Long memRoleIdx, int memGradeIdx) {
//    	
//    	Long memRoleIdx = memberMapper.findDefaultRoleIdx(); 	// Duplicate local variable memRoleIdx
//    	int memGradeIdx = 1;  // grade도 같은 방식 가능
//    	
//    	// 21번 - err he method insertMember(String, String, String, String, String, String, Long, int) in the type MemberMapper is not applicable for the arguments (String, String, String, String, String)
//        return memberMapper.insertMember(memId, memPwd, memName, memTel, memEmail, memIp);	
//    } 
 
//    // 아이디 중복 체크
//    public boolean isDuplicate(String memId) {
//        int count = memberMapper.countByMemId(memId);
//        return count > 0;
//    }
  
    // 로그인 처리
    public int loginMember(String memId, String memPwd) {
        return memberMapper.loginMember(memId, memPwd);
    }

}
