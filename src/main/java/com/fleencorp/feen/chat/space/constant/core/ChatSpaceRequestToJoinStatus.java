package com.fleencorp.feen.chat.space.constant.core;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
 * Enum representing the different statuses for a request to join a chat space.
 *
 * <p>This enum defines the various states that a join request can have, allowing for
 * tracking and management of member requests within chat spaces.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum ChatSpaceRequestToJoinStatus implements ApiParameter {

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

  /**
   * Checks if the given request status is approved.
   *
   * <p>Returns true if the status is APPROVED, otherwise returns false.</p>
   *
   * @param chatSpaceRequestToJoinStatus the status to check.
   * @return true if the status is APPROVED, otherwise false.
   */
  public static boolean isApproved(final ChatSpaceRequestToJoinStatus chatSpaceRequestToJoinStatus) {
    return APPROVED == chatSpaceRequestToJoinStatus;
  }

  /**
   * Checks if the given request status is disapproved.
   *
   * <p>Returns true if the status is DISAPPROVED, otherwise returns false.</p>
   *
   * @param chatSpaceRequestToJoinStatus the status to check.
   * @return true if the status is DISAPPROVED, otherwise false.
   */
  public static boolean isDisapproved(final ChatSpaceRequestToJoinStatus chatSpaceRequestToJoinStatus) {
    return DISAPPROVED == chatSpaceRequestToJoinStatus;
  }

  /**
   * Checks if the given request status is disapproved or pending.
   *
   * <p>Returns true if the status is DISAPPROVED or PENDING, otherwise returns false.</p>
   *
   * @param chatSpaceRequestToJoinStatus the status to check.
   * @return true if the status is DISAPPROVED or PENDING, otherwise false.
   */
  public static boolean isDisapprovedOrPending(final ChatSpaceRequestToJoinStatus chatSpaceRequestToJoinStatus) {
    return DISAPPROVED == chatSpaceRequestToJoinStatus || PENDING == chatSpaceRequestToJoinStatus;
  }

  /**
   * Converts a string value to a corresponding {@link ChatSpaceRequestToJoinStatus} enum.
   *
   * <p>If the provided value does not match any enum constants, null is returned.</p>
   *
   * @param value the string representation of the desired enum value.
   * @return the corresponding {@link ChatSpaceRequestToJoinStatus} enum, or null if the value does not match any constants.
   */
  public static ChatSpaceRequestToJoinStatus of(final String value) {
    return parseEnumOrNull(value, ChatSpaceRequestToJoinStatus.class);
  }

  /**
   * Returns the approved status for a request to join a chat space.
   * This method is used to retrieve the predefined status that indicates the request has been approved.
   *
   * @return The {@link ChatSpaceRequestToJoinStatus} representing the approved status.
   */
  public static ChatSpaceRequestToJoinStatus approved() {
    return APPROVED;
  }
}
