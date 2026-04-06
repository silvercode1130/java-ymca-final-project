package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; 

import com.springbootstudy.bbs.domain.ReviewVO;

@Mapper
public interface ReviewMapper {
	
	// 검색 기능
	List<ReviewVO> search(@Param("searchType") String searchType,
            @Param("keyword") String keyword);
	
}
