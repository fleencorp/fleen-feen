package com.fleencorp.feen.model.response.stream.attendance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "stream_id",
  "request_to_join_status_info",
  "stream_type_info",
  "stream"
})
public class ProcessAttendeeRequestToJoinStreamResponse extends ApiResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("request_to_join_status_info")
  private StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo;

  @JsonProperty("stream_type_info")
  protected StreamTypeInfo streamTypeInfo;

  @JsonProperty("stream")
  private FleenStreamResponse stream;

  @JsonIgnore
  protected StreamType getStreamType() {
    return nonNull(streamTypeInfo) ? streamTypeInfo.getStreamType() : null;
  }

  @Override
  public String getMessageCode() {
    return StreamType.isEvent(getStreamType())
      ? "process.attendee.request.to.join.event"
      : "process.attendee.request.to.join.live.broadcast";
  }

  public static ProcessAttendeeRequestToJoinStreamResponse of(final Long streamId, final StreamTypeInfo streamTypeInfo, final FleenStreamResponse stream) {
    return new ProcessAttendeeRequestToJoinStreamResponse(streamId, null, streamTypeInfo, stream);
  }
}
