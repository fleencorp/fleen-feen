package com.fleencorp.feen.stream.model.response.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
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
@JsonPropertyOrder({
  "message",
  "stream_id",
  "stream_type_info",
  "is_deleted_info"
})
public class DeleteStreamResponse extends LocalizedResponse {

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

  public static DeleteStreamResponse of(final long streamId, final StreamTypeInfo streamTypeInfo, final IsDeletedInfo isDeletedInfo) {
    return new DeleteStreamResponse(streamId, streamTypeInfo, isDeletedInfo);
  }
}
