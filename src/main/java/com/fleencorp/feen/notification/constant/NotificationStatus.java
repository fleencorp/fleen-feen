package com.fleencorp.feen.notification.constant;

/**
 * Enum representing the status of a notification.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public enum NotificationStatus {

  READ,
  UNREAD;

  /**
   * Returns the unread notification status.
   *
   * @return the {@link NotificationStatus} representing unread.
   */
  public static NotificationStatus unread() {
    return NotificationStatus.UNREAD;
  }

  /**
   * Returns the read notification status.
   *
   * @return the {@link NotificationStatus} representing read.
   */
  public static NotificationStatus read() {
    return NotificationStatus.READ;
  }
}

