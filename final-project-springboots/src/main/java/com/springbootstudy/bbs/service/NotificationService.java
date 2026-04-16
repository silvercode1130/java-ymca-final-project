package com.springbootstudy.bbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.domain.NotificationVO;
import com.springbootstudy.bbs.mapper.NotificationMapper;

@Service
public class NotificationService {

	@Autowired
	private NotificationMapper notificationMapper;

	// 알림 저장
	public void sendNotification(NotificationVO notification) {
		notificationMapper.insertNotification(notification);
	}

	// 회원별 알림 전체 조회
	public List<NotificationVO> getNotificationsForMember(Long memIdx) {
		return notificationMapper.selectNotificationsByMember(memIdx);
	}

	// 단건 읽음 처리
	public void markAsRead(Long notificationIdx) {
		notificationMapper.updateNotificationRead(notificationIdx);
	}

	// 전체 읽음 처리
	public void markAllAsRead(Long memIdx) {
		notificationMapper.updateAllNotificationsRead(memIdx);
	}

	// 안 읽은 알림 존재 여부 (Y/N만 필요)
	public boolean hasUnread(Long memIdx) {
		return notificationMapper.countUnread(memIdx) > 0;
	}

	// 안 읽은 알림 개수 (정확한 갯수가 필요)
	public int getUnreadCount(Long memIdx) {
		return notificationMapper.countUnread(memIdx);
	}
}