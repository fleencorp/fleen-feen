package com.fleencorp.feen.notification.service;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.notification.model.domain.Notification;
import com.fleencorp.feen.notification.model.response.ReadNotificationResponse;
import com.fleencorp.feen.notification.model.search.NotificationSearchResult;
import com.fleencorp.feen.shared.security.RegisteredUser;

public interface NotificationService {

  NotificationSearchResult findNotifications(SearchRequest searchRequest, RegisteredUser user);

  void save(Notification notification);

  ReadNotificationResponse markAsRead(Long notificationId, RegisteredUser user);

  ReadNotificationResponse markAllAsRead(RegisteredUser user);
}
