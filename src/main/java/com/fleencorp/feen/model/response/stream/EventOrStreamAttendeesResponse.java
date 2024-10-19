package com.fleencorp.feen.model.response.stream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.*;
import org.springframework.data.domain.Page;

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
  "stream_id",
  "attendees",
  "page"
})
public class EventOrStreamAttendeesResponse extends ApiResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @JsonProperty("stream_id")
  private Long streamId;

  @Builder.Default
  @JsonProperty("attendees")
  private List<EventOrStreamAttendeeResponse> attendees = new ArrayList<>();

  @JsonProperty("page")
  private Page<?> page;

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
