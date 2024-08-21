package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
* Enum representing different status of an attendee or guest's request to join a stream or event
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
}
