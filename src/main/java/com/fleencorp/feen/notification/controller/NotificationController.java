package com.fleencorp.feen.notification.controller;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.notification.model.response.ReadNotificationResponse;
import com.fleencorp.feen.notification.model.search.NotificationSearchResult;
import com.fleencorp.feen.notification.service.NotificationService;
import com.fleencorp.feen.shared.security.RegisteredUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/notification")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(final NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping(value = "/entries")
  public NotificationSearchResult findNotifications(
      @SearchParam final SearchRequest searchRequest,
      @AuthenticationPrincipal final RegisteredUser user) {
    return notificationService.findNotifications(searchRequest, user);
  }

  @PutMapping(value = "/mark-read/{notificationId}")
  public ReadNotificationResponse markAsRead(
      @PathVariable(name = "notificationId") final Long notificationId,
      @AuthenticationPrincipal final RegisteredUser user) {
    return notificationService.markAsRead(notificationId, user);
  }

  @PutMapping(value = "/mark-all-read")
  public ReadNotificationResponse markAllAsRead(
      @AuthenticationPrincipal final RegisteredUser user) {
    return notificationService.markAllAsRead(user);
  }
}
