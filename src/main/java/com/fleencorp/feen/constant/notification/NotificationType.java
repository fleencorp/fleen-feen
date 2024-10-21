package com.fleencorp.feen.constant.notification;

import lombok.Getter;

/**
 * Enum representing various types of notifications that can be sent to users.
 *
 * <p>Each constant in this enum represents a specific notification type that the
 * system can generate, ranging from contact requests to event participation
 * notifications. The notification types can be used to categorize and handle
 * different notification events within the application.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum NotificationType {

  REQUEST_TO_JOIN_CHAT_SPACE_APPROVED("request.to.join.chat.space.approved"),
  REQUEST_TO_JOIN_CHAT_SPACE_DISAPPROVED("request.to.join.chat.space.disapproved"),
  REQUEST_TO_JOIN_CHAT_SPACE_RECEIVED("request.to.join.chat.space.received"),
  REQUEST_TO_JOIN_EVENT_APPROVED("request.to.join.event.approved"),
  REQUEST_TO_JOIN_EVENT_DISAPPROVED("request.to.join.event.disapproved"),
  REQUEST_TO_JOIN_EVENT_RECEIVED("request.to.join.event.received"),
  REQUEST_TO_JOIN_LIVE_BROADCAST_APPROVED("request.to.join.live.broadcast.approved"),
  REQUEST_TO_JOIN_LIVE_BROADCAST_DISAPPROVED("request.to.join.live.broadcast.disapproved"),
  REQUEST_TO_JOIN_LIVE_BROADCAST_RECEIVED("request.to.join.live.broadcast.received"),
  SHARE_CONTACT_REQUEST_APPROVED("share.contact.request.approved"),
  SHARE_CONTACT_REQUEST_DISAPPROVED("share.contact.request.disapproved"),
  SHARE_CONTACT_REQUEST_RECEIVED("share.contact.request.received"),
  USER_FOLLOWING("user.following"),;

  private final String code;

  NotificationType(final String code) {
    this.code = code;
  }

  /**
   * Retrieves the {@link NotificationType} representing the approval of a request to join a chat space.
   *
   * @return {@link NotificationType#REQUEST_TO_JOIN_CHAT_SPACE_APPROVED}
   */
  public static NotificationType requestToJoinChatSpaceApproved() {
    return REQUEST_TO_JOIN_CHAT_SPACE_APPROVED;
  }

  /**
   * Retrieves the {@link NotificationType} representing the disapproval of a request to join a chat space.
   *
   * @return {@link NotificationType#REQUEST_TO_JOIN_CHAT_SPACE_DISAPPROVED}
   */
  public static NotificationType requestToJoinChatSpaceDisapproved() {
    return REQUEST_TO_JOIN_CHAT_SPACE_DISAPPROVED;
  }

  /**
   * Retrieves the {@link NotificationType} representing the receipt of a request to join a chat space.
   *
   * @return {@link NotificationType#REQUEST_TO_JOIN_CHAT_SPACE_RECEIVED}
   */
  public static NotificationType requestToJoinChatSpaceReceived() {
    return REQUEST_TO_JOIN_CHAT_SPACE_RECEIVED;
  }

  /**
   * Retrieves the {@link NotificationType} representing the approval of a request to join an event.
   *
   * @return {@link NotificationType#REQUEST_TO_JOIN_EVENT_APPROVED}
   */
  public static NotificationType requestToJoinEventApproved() {
    return REQUEST_TO_JOIN_EVENT_APPROVED;
  }

  /**
   * Retrieves the {@link NotificationType} representing the disapproval of a request to join an event.
   *
   * @return {@link NotificationType#REQUEST_TO_JOIN_EVENT_DISAPPROVED}
   */
  public static NotificationType requestToJoinEventDisapproved() {
    return REQUEST_TO_JOIN_EVENT_DISAPPROVED;
  }

  /**
   * Retrieves the {@link NotificationType} representing the receipt of a request to join an event.
   *
   * @return {@link NotificationType#REQUEST_TO_JOIN_EVENT_RECEIVED}
   */
  public static NotificationType requestToJoinEventReceived() {
    return REQUEST_TO_JOIN_EVENT_RECEIVED;
  }

  /**
   * Retrieves the {@link NotificationType} representing the approval of a request to join a live broadcast.
   *
   * @return {@link NotificationType#REQUEST_TO_JOIN_LIVE_BROADCAST_APPROVED}
   */
  public static NotificationType requestToJoinLiveBroadcastApproved() {
    return REQUEST_TO_JOIN_LIVE_BROADCAST_APPROVED;
  }

  /**
   * Retrieves the {@link NotificationType} representing the disapproval of a request to join a live broadcast.
   *
   * @return {@link NotificationType#REQUEST_TO_JOIN_LIVE_BROADCAST_DISAPPROVED}
   */
  public static NotificationType requestToJoinLiveBroadcastDisapproved() {
    return REQUEST_TO_JOIN_LIVE_BROADCAST_DISAPPROVED;
  }

  /**
   * Retrieves the {@link NotificationType} representing the receipt of a request to join a live broadcast.
   *
   * @return {@link NotificationType#REQUEST_TO_JOIN_LIVE_BROADCAST_RECEIVED}
   */
  public static NotificationType requestToJoinLiveBroadcastReceived() {
    return REQUEST_TO_JOIN_LIVE_BROADCAST_RECEIVED;
  }

  /**
   * Retrieves the {@link NotificationType} representing the approval of a share contact request.
   *
   * @return {@link NotificationType#SHARE_CONTACT_REQUEST_APPROVED}
   */
  public static NotificationType shareContactRequestApproved() {
    return SHARE_CONTACT_REQUEST_APPROVED;
  }

  /**
   * Retrieves the {@link NotificationType} representing the disapproval of a share contact request.
   *
   * @return {@link NotificationType#SHARE_CONTACT_REQUEST_DISAPPROVED}
   */
  public static NotificationType shareContactRequestDisapproved() {
    return SHARE_CONTACT_REQUEST_DISAPPROVED;
  }

  /**
   * Retrieves the {@link NotificationType} representing the receipt of a share contact request.
   *
   * @return {@link NotificationType#SHARE_CONTACT_REQUEST_RECEIVED}
   */
  public static NotificationType shareContactRequestReceived() {
    return SHARE_CONTACT_REQUEST_RECEIVED;
  }

  /**
   * Retrieves the {@link NotificationType} representing the action of a user being followed.
   *
   * @return {@link NotificationType#USER_FOLLOWING}
   */
  public static NotificationType userFollowing() {
    return USER_FOLLOWING;
  }
}
