package com.fleencorp.feen.exception.notification;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class NotificationNotFoundException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "notification.not.found";
  }

  public NotificationNotFoundException(final Object...params) {
    super(params);
  }

  public static Supplier<NotificationNotFoundException> of(final Object notificationId) {
    return () -> new NotificationNotFoundException(notificationId);
  }
}
