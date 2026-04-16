package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.springbootstudy.bbs.domain.NotificationVO;

@Mapper
public interface NotificationMapper {

    int insertNotification(NotificationVO notification);

    List<NotificationVO> selectNotificationsByMember(Long memIdx);

    int updateNotificationRead(Long notificationIdx);

    int updateAllNotificationsRead(Long memIdx);
    
    int countUnread(Long memIdx);
}
