package com.fleencorp.feen.stream.model.response.statistic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.stream.constant.core.StreamType;
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
  "total_count",
  "stream_type_info"
})
public class TotalStreamsAttendedByUserResponse extends LocalizedResponse {

  @JsonProperty("total_count")
  private Long totalCount;

  @JsonProperty("stream_type_info")
  protected StreamTypeInfo streamTypeInfo;

  @JsonIgnore
  protected StreamType getStreamType() {
    return nonNull(streamTypeInfo) ? streamTypeInfo.getStreamType() : null;
  }

  @Override
  public String getMessageCode() {
    return StreamType.isEvent(getStreamType()) ? "total.event.attended.by.user" : "total.live.broadcast.attended.by.user";
  }

  public static TotalStreamsAttendedByUserResponse of(final Long totalCount, final StreamTypeInfo streamTypeInfo) {
    return new TotalStreamsAttendedByUserResponse(totalCount, streamTypeInfo);
  }
}
