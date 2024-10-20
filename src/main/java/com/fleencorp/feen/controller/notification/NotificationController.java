package com.fleencorp.feen.controller.notification;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.response.notification.ReadNotificationResponse;
import com.fleencorp.feen.model.search.notification.NotificationSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.notification.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/notification")
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(final NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping(value = "/entries")
  public NotificationSearchResult findNotifications(
      @SearchParam final SearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return notificationService.findNotifications(searchRequest, user);
  }

  @PutMapping(value = "/mark-read/{notificationId}")
  public ReadNotificationResponse markAsRead(
      @PathVariable(name = "notificationId") final Long notificationId,
      @AuthenticationPrincipal final FleenUser user) {
    return notificationService.markAsRead(notificationId, user);
  }

  @PutMapping(value = "/mark-all-read")
  public ReadNotificationResponse markAllAsRead(
      @AuthenticationPrincipal final FleenUser user) {
    return notificationService.markAllAsRead(user);
  }
}
