package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
public class EventAttendeesResponse {

  @JsonProperty("event_id")
  private long eventId;

  @Builder.Default
  @JsonProperty("message")
  private String message = "Attendees retrieved successfully";

  @Builder.Default
  @JsonProperty("attendees")
  private List<EventAttendeeResponse> attendees = new ArrayList<>();
}
