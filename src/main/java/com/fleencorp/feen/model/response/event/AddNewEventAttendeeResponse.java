package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.event.base.EventResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "event_id",
  "email_address",
  "event"
})
public class AddNewEventAttendeeResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @JsonProperty("email_address")
  private String emailAddress;

  @JsonProperty("event")
  private FleenStreamResponse event;

  @Builder.Default
  @JsonProperty("message")
  private String message = "Attendee added successfully";

  public static AddNewEventAttendeeResponse of(final Long eventId, final EventResponse event, final String emailAddress) {
    return AddNewEventAttendeeResponse.builder()
            .eventId(eventId)
            .emailAddress(emailAddress)
            .event(event)
            .build();
  }
}
