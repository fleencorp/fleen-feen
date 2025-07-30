package com.fleencorp.feen.stream.model.response.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.model.info.core.StreamStatusInfo;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
import com.fleencorp.localizer.model.response.LocalizedResponse;
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
  "stream_status_info"
})
public class CancelStreamResponse extends LocalizedResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("stream_status_info")
  private StreamStatusInfo streamStatusInfo;

  @JsonProperty("stream_type_info")
  protected StreamTypeInfo streamTypeInfo;

  @JsonIgnore
  protected StreamType getStreamType() {
    return nonNull(streamTypeInfo) ? streamTypeInfo.getStreamType() : null;
  }

  @Override
  public String getMessageCode() {
    return StreamType.isEvent(getStreamType()) ? "cancel.event" : "cancel.live.broadcast";
  }

  public static CancelStreamResponse of(final long eventId, final StreamStatusInfo streamStatusInfo, final StreamTypeInfo streamTypeInfo) {
    return new CancelStreamResponse(eventId, streamStatusInfo, streamTypeInfo);
  }

}
