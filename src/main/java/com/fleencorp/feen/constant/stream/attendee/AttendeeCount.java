package com.fleencorp.feen.constant.stream.attendee;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum AttendeeCount implements ApiParameter {

  TOTAL_ATTENDEE("total.attendee.count", "total.attendees.count.2", "total.attendees.count.otherText");

  private final String value;
  private final String messageCode2;
  private final String messageCode3;

  AttendeeCount(
      final String value,
      final String messageCode2,
      final String messageCode3) {
    this.value = value;
    this.messageCode2 = messageCode2;
    this.messageCode3 = messageCode3;
  }

  public String getMessageCode() {
    return value;
  }

  public static AttendeeCount totalAttendee() {
    return TOTAL_ATTENDEE;
  }
}
