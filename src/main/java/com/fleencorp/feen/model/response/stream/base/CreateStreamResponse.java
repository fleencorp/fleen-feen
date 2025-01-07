package com.fleencorp.feen.model.response.stream.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
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
  "stream_type_info",
  "stream"
})
public class CreateStreamResponse extends ApiResponse {

  @JsonProperty("stream_id")
  protected Long streamId;

  @JsonProperty("stream_type_info")
  protected StreamTypeInfo streamTypeInfo;

  @JsonProperty("stream")
  protected FleenStreamResponse stream;

  @JsonIgnore
  protected StreamType getStreamType() {
    return nonNull(streamTypeInfo) ? streamTypeInfo.getStreamType() : null;
  }

  @Override
  public String getMessageCode() {
    return StreamType.isEvent(getStreamType()) ? "create.event" : "create.live.broadcast";
  }

  public static CreateStreamResponse of(final Long streamId, final StreamTypeInfo streamTypeInfo, final FleenStreamResponse stream) {
    return new CreateStreamResponse(streamId, streamTypeInfo, stream);
  }

}
