package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
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

  PENDING("Pending"),
  JOINED("Joined"),
  NOT_JOINED("Not Joined"),
  DISAPPROVED("Disapproved");

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
}
