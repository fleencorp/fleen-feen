package com.fleencorp.feen.model.response.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "attendee_id",
  "full_name"
})
public class StreamAttendeeResponse {

  @JsonProperty("attendee_id")
  private Long attendeeId;

  @JsonProperty("full_name")
  private String fullName;

  public static StreamAttendeeResponse of(final Long attendeeId, final String fullName) {
    return StreamAttendeeResponse.builder()
        .attendeeId(attendeeId)
        .fullName(fullName)
        .build();
  }
}
