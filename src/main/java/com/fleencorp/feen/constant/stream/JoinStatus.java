package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import lombok.Getter;

/**
 * Enum representing different join statuses for stream attendees.
 *
 * <p>This enum does not currently define any specific constants, but provides a static method to
 * map a {@link StreamAttendeeRequestToJoinStatus} to a string representation of the join status.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum JoinStatus implements ApiParameter {

  DISAPPROVED("Disapproved"),
  JOINED("Joined"),
  NOT_JOINED("Join"),
  PENDING("Pending");

  private final String value;

  JoinStatus(final String value) {
    this.value = value;
  }

  /**
   * Maps the given {@link StreamAttendeeRequestToJoinStatus} to a string representation.
   *
   * <p>This method converts the provided {@code joinStatus} to a human-readable string.
   * If the status is {@code PENDING}, it returns "Pending". If the status is {@code APPROVED},
   * it returns "Joined". For any other status, it returns {@code null}.</p>
   *
   * @param joinStatus the status of a stream attendee request to join.
   * @return the string representation of the join status, or {@code null} if the status is not recognized.
   */
  public static String getJoinStatus(final StreamAttendeeRequestToJoinStatus joinStatus) {
    return switch (joinStatus) {
      case PENDING -> PENDING.getValue();
      case APPROVED -> JOINED.getValue();
      case DISAPPROVED -> DISAPPROVED.getValue();
    };
  }

  /**
   * Retrieves the appropriate status label for a given join status.
   *
   * <p>This method returns a user-friendly status string for the provided
   * {@link ChatSpaceRequestToJoinStatus}. Based on the status, it will return:
   * "Pending" for PENDING, "Joined" for APPROVED, and "Disapproved" for DISAPPROVED.</p>
   *
   * @param joinStatus the {@link ChatSpaceRequestToJoinStatus} to get the status label for
   * @return the corresponding status string for the given join status
   */
  public static String getJoinStatus(final ChatSpaceRequestToJoinStatus joinStatus) {
    return switch (joinStatus) {
      case PENDING -> PENDING.getValue();
      case APPROVED -> JOINED.getValue();
      case DISAPPROVED -> DISAPPROVED.getValue();
    };
  }

  /**
   * Checks if the provided join status indicates that it is not approved.
   *
   * <p>This method returns {@code true} if the given {@code joinStatus} string is
   * not equivalent to the "Joined" status, meaning the user has not been approved.
   * Otherwise, it returns {@code false}.</p>
   *
   * @param joinStatus the status string to check
   * @return {@code true} if the status is not "Joined", {@code false} otherwise
   */
  public static boolean isNotApproved(final String joinStatus) {
    return !(JOINED.getValue().equals(joinStatus));
  }
}
