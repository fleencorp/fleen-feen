package com.fleencorp.feen.model.response.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.response.stream.StreamAttendeeResponse;
import com.fleencorp.feen.model.response.stream.base.FleenStreamResponse;
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
  "stream_id",
  "stream",
  "attendees",
  "total_attending"
})
public class RetrieveStreamResponse extends ApiResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("stream")
  private FleenStreamResponse stream;

  @JsonProperty("attendees")
  private Set<StreamAttendeeResponse> attendees;

  @JsonProperty("total_attending")
  private Long totalAttending;

  @Override
  public String getMessageCode() {
    return "retrieve.stream";
  }

  public static RetrieveStreamResponse of(final Long streamId, final FleenStreamResponse stream, final Set<StreamAttendeeResponse> attendees, final Long totalAttending) {
    return RetrieveStreamResponse.builder()
            .streamId(streamId)
            .stream(stream)
            .attendees(attendees)
            .totalAttending(totalAttending)
            .build();
  }
}
