package com.springbootstudy.bbs.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.springbootstudy.bbs.domain.AlarmVO;

@Mapper
public interface AlarmMapper {

    int insertAlarm(AlarmVO alarm);
}
