  package com.fleencorp.feen.common.constant.social;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
 * Enum representing the status of a contact share request.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum ShareContactRequestStatus implements ApiParameter {

  ACCEPTED("Confirmed", "share.contact.request.status.accepted", "share.contact.request.status.accepted.2"),
  CANCELED("Canceled", "share.contact.request.status.canceled", "share.contact.request.status.canceled.2"),
  REJECTED("Rejected", "share.contact.request.status.rejected", "share.contact.request.status.rejected.2"),
  SENT("Sent", "share.contact.request.status.sent", "share.contact.request.status.sent.2");

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  ShareContactRequestStatus(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static ShareContactRequestStatus of(final String value) {
    return parseEnumOrNull(value, ShareContactRequestStatus.class);
  }

  /**
   * Checks if the specified {@link ShareContactRequestStatus} is ACCEPTED.
   *
   * @param status the share contact request status to check
   * @return {@code true} if the {@code status} is ACCEPTED; {@code false} otherwise
   */
  public static boolean isAccepted(final ShareContactRequestStatus status) {
    return status == ACCEPTED;
  }

  /**
   * Checks if the specified {@link ShareContactRequestStatus} is REJECTED.
   *
   * @param status the share contact request status to check
   * @return {@code true} if the {@code status} is REJECTED; {@code false} otherwise
   */
  public static boolean isRejected(final ShareContactRequestStatus status) {
    return status == REJECTED;
  }

  /**
   * Checks if the specified {@link ShareContactRequestStatus} is CANCELED.
   *
   * @param status the share contact request status to check
   * @return {@code true} if the {@code status} is CANCELED; {@code false} otherwise
   */
  public static boolean isCanceled(final ShareContactRequestStatus status) {
    return status == CANCELED;
  }

  /**
   * Checks if the specified {@link ShareContactRequestStatus} is either ACCEPTED or REJECTED.
   *
   * @param status the share contact request status to check
   * @return {@code true} if the {@code status} is either ACCEPTED or REJECTED; {@code false} otherwise
   */
  public static boolean isAcceptedOrRejected(final ShareContactRequestStatus status) {
    return status == ACCEPTED || status == REJECTED;
  }

  /**
   * Checks if the specified {@link ShareContactRequestStatus} is either SENT or CANCELED.
   *
   * @param status the share contact request status to check
   * @return {@code true} if the {@code status} is either SENT or CANCELED; {@code false} otherwise
   */
  public static boolean isSentOrCanceled(final ShareContactRequestStatus status) {
    return status == SENT || status == CANCELED;
  }

}
