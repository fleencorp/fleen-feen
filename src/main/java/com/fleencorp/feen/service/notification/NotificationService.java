package com.fleencorp.feen.service.notification;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.response.notification.ReadNotificationResponse;
import com.fleencorp.feen.model.search.notification.NotificationSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface NotificationService {

  NotificationSearchResult findNotifications(SearchRequest searchRequest, FleenUser user);

  void save(Notification notification);

  ReadNotificationResponse markAsRead(final Long notificationId, FleenUser user);

  ReadNotificationResponse markAllAsRead(FleenUser user);
}
