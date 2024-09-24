package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
* Enum representing different status of an attendee or guest's request to join a event or stream
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum StreamAttendeeRequestToJoinStatus implements ApiParameter {

  APPROVED("APPROVED"),
  DISAPPROVED("DISAPPROVED"),
  PENDING("PENDING");

  private final String value;

  StreamAttendeeRequestToJoinStatus(final String value) {
    this.value = value;
  }

  public static StreamAttendeeRequestToJoinStatus of(final String value) {
    return parseEnumOrNull(value, StreamAttendeeRequestToJoinStatus.class);
  }

  /**
   * Checks if the given status indicates that the request to join the stream has been approved.
   *
   * @param status the status of the request to join
   * @return {@code true} if the status is approved; {@code false} otherwise
   */
  public static boolean isApproved(final StreamAttendeeRequestToJoinStatus status) {
    return APPROVED == status;
  }

  /**
   * Checks if the given status indicates that the request to join the stream is pending.
   *
   * @param status the status of the request to join
   * @return {@code true} if the status is pending; {@code false} otherwise
   */
  public static boolean isPending(final StreamAttendeeRequestToJoinStatus status) {
    return PENDING == status;
  }

  /**
   * Checks if the given status indicates that the request to join the stream has been disapproved.
   *
   * @param status the status of the request to join
   * @return {@code true} if the status is disapproved; {@code false} otherwise
   */
  public static boolean isDisapproved(final StreamAttendeeRequestToJoinStatus status) {
    return DISAPPROVED == status;
  }

}
