package com.fleencorp.feen.exception.notification;

import com.fleencorp.base.exception.FleenException;

import java.util.function.Supplier;

public class NotificationNotFoundException extends FleenException {

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
