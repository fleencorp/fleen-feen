package com.fleencorp.feen.stream.model.projection;

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
  private String username;

  public String getFullName() {
    return firstName + " " + lastName;
  }

}
