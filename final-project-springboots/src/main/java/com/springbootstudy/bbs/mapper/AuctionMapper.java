package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.springbootstudy.bbs.domain.AuctionListDTO;
import com.springbootstudy.bbs.domain.AuctionVO;

@Mapper
public interface AuctionMapper {
	List<AuctionListDTO> auctionList();
}
