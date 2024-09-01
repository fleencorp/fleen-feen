package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.base.ApiResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.StreamAttendeeResponse;
import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "event_id",
  "event",
  "attendees",
  "total_attending"
})
public class RetrieveEventResponse extends ApiResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @JsonProperty("event")
  private FleenStreamResponse event;

  @JsonProperty("attendees")
  private Set<StreamAttendeeResponse> attendees;

  @JsonProperty("total_attending")
  private Long totalAttending;

  @Override
  public String getMessageKey() {
    return "retrieve.event";
  }

  public static RetrieveEventResponse of(final Long eventId, final FleenStreamResponse event, final Set<StreamAttendeeResponse> attendees, final Long totalAttending) {
    return RetrieveEventResponse.builder()
            .eventId(eventId)
            .event(event)
            .attendees(attendees)
            .totalAttending(totalAttending)
            .build();
  }
}
