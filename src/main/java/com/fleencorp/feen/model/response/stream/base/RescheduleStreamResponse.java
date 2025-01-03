package com.fleencorp.feen.model.response.stream.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "stream_id",
  "stream_type_info",
  "stream"
})
public class RescheduleStreamResponse extends ApiResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("stream")
  private FleenStreamResponse stream;

  @JsonProperty("stream_type_info")
  protected StreamTypeInfo streamTypeInfo;

  @JsonIgnore
  protected StreamType getStreamType() {
    return nonNull(streamTypeInfo) ? streamTypeInfo.getStreamType() : null;
  }

  @Override
  public String getMessageCode() {
    return StreamType.isEvent(getStreamType()) ? "reschedule.event" : "reschedule.live.broadcast";
  }

  public static RescheduleStreamResponse of(final long streamId, final FleenStreamResponse stream, final StreamTypeInfo streamTypeInfo) {
    return new RescheduleStreamResponse(streamId, stream, streamTypeInfo);
  }

}
