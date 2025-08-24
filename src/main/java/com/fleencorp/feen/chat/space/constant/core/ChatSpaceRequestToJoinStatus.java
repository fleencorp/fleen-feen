package com.fleencorp.feen.chat.space.constant.core;

import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum ChatSpaceRequestToJoinStatus {

  APPROVED("Approved", "chat.space.request.to.join.status.approved"),
  DISAPPROVED("Disapproved", "chat.space.request.to.join.status.disapproved"),
  PENDING("Pending", "chat.space.request.to.join.status.pending");

  private final String value;
  private final String messageCode;

  ChatSpaceRequestToJoinStatus(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  public static boolean isApproved(final ChatSpaceRequestToJoinStatus requestToJoinStatus) {
    return requestToJoinStatus == APPROVED;
  }

  public static boolean isDisapproved(final ChatSpaceRequestToJoinStatus requestToJoinStatus) {
    return requestToJoinStatus == DISAPPROVED;
  }

  public static boolean isDisapprovedOrPending(final ChatSpaceRequestToJoinStatus requestToJoinStatus) {
    return requestToJoinStatus == DISAPPROVED || requestToJoinStatus == PENDING;
  }

  public static boolean isPending(final ChatSpaceRequestToJoinStatus requestToJoinStatus) {
    return requestToJoinStatus == PENDING;
  }

  public static ChatSpaceRequestToJoinStatus of(final String value) {
    return parseEnumOrNull(value, ChatSpaceRequestToJoinStatus.class);
  }

  public static ChatSpaceRequestToJoinStatus approved() {
    return APPROVED;
  }
}
