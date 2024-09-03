package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.base.ApiResponse;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "event_id",
  "attendees"
})
public class EventAttendeesResponse extends ApiResponse {

  @JsonProperty("event_id")
  private long eventId;

  @Builder.Default
  @JsonProperty("attendees")
  private List<EventAttendeeResponse> attendees = new ArrayList<>();

  @Override
  public String getMessageKey() {
    return "event.attendees";
  }

  public static EventAttendeesResponse of() {
    return EventAttendeesResponse.builder()
      .build();
  }
}
