package com.fleencorp.feen.service.notification;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.response.notification.ReadNotificationResponse;
import com.fleencorp.feen.model.search.notification.NotificationSearchResult;
import com.fleencorp.feen.user.security.RegisteredUser;

public interface NotificationService {

  NotificationSearchResult findNotifications(SearchRequest searchRequest, RegisteredUser user);

  void save(Notification notification);

  ReadNotificationResponse markAsRead(Long notificationId, RegisteredUser user);

  ReadNotificationResponse markAllAsRead(RegisteredUser user);
}
