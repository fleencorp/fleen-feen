package com.fleencorp.feen.model.response.stream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
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
public class EventOrStreamAttendeesResponse extends ApiResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @JsonProperty("stream_id")
  private Long streamId;

  @Builder.Default
  @JsonProperty("attendees")
  private List<EventOrStreamAttendeeResponse> attendees = new ArrayList<>();

  @Override
  public String getMessageCode() {
    return "event.or.stream.attendees";
  }

  public static EventOrStreamAttendeesResponse of(final Long eventIdOrStreamId) {
    return EventOrStreamAttendeesResponse.builder()
      .eventId(eventIdOrStreamId)
      .streamId(eventIdOrStreamId)
      .build();
  }
}
