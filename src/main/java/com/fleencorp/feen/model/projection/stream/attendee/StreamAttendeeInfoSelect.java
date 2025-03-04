package com.fleencorp.feen.model.projection.stream.attendee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StreamAttendeeInfoSelect {

  private Long attendeeId;
  private Long streamId;
  private String firstName;
  private String lastName;
  private String emailAddress;

  public String getFullName() {
    return firstName + " " + lastName;
  }

}
