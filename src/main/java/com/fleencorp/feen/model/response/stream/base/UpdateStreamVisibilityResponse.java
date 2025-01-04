package com.fleencorp.feen.model.response.stream.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.StreamVisibilityInfo;
import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "stream_id",
  "visibility_info",
  "stream_type_info"
})
public class UpdateStreamVisibilityResponse extends ApiResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("visibility_info")
  private StreamVisibilityInfo streamVisibilityInfo;

  @JsonProperty("stream_type_info")
  protected StreamTypeInfo streamTypeInfo;

  @JsonIgnore
  protected StreamType getStreamType() {
    return nonNull(streamTypeInfo) ? streamTypeInfo.getStreamType() : null;
  }

  @Override
  public String getMessageCode() {
    return StreamType.isEvent(getStreamType()) ? "update.event.visibility" : "update.live.broadcast.visibility";
  }

  public static UpdateStreamVisibilityResponse of(final Long eventId, final StreamVisibilityInfo streamVisibilityInfo, final StreamTypeInfo streamTypeInfo) {
    return new UpdateStreamVisibilityResponse(eventId, streamVisibilityInfo, streamTypeInfo);
  }
}
