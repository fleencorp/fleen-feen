  package com.fleencorp.feen.constant.social;

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

  CANCELED("Canceled"),
  ACCEPTED("Confirmed"),
  REJECTED("Rejected"),
  SENT("Sent");

  private final String value;

  ShareContactRequestStatus(final String value) {
    this.value = value;
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
