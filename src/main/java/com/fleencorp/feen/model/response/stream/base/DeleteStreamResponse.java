package com.fleencorp.feen.model.response.stream.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.info.IsDeletedInfo;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import lombok.*;

import static java.util.Objects.nonNull;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "message",
  "stream_id",
  "stream_type_info",
  "is_deleted_info"
})
public class DeleteStreamResponse extends ApiResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("stream_type_info")
  protected StreamTypeInfo streamTypeInfo;

  @JsonProperty("is_deleted_info")
  private IsDeletedInfo deletedInfo;

  @JsonIgnore
  protected StreamType getStreamType() {
    return nonNull(streamTypeInfo) ? streamTypeInfo.getStreamType() : null;
  }

  @Override
  public String getMessageCode() {
    return StreamType.isEvent(getStreamType()) ? "delete.event" : "delete.live.broadcast";
  }

  public static DeleteStreamResponse of(final long eventId, final StreamTypeInfo streamTypeInfo, final IsDeletedInfo isDeletedInfo) {
    return new DeleteStreamResponse(eventId, streamTypeInfo, isDeletedInfo);
  }
}
