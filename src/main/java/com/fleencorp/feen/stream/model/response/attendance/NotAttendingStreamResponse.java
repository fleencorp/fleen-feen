package com.fleencorp.feen.stream.model.response.attendance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.common.model.info.JoinStatusInfo;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.model.info.attendee.IsAttendingInfo;
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
  "is_attending_info",
  "join_status_info",
  "stream_type_info"
})
public class NotAttendingStreamResponse extends LocalizedResponse {

  @JsonProperty("is_attending_info")
  private IsAttendingInfo attendingInfo;

  @JsonProperty("join_status_info")
  private JoinStatusInfo joinStatusInfo;

  @JsonProperty("stream_type_info")
  protected StreamTypeInfo streamTypeInfo;

  @JsonIgnore
  protected StreamType getStreamType() {
    return nonNull(streamTypeInfo) ? streamTypeInfo.getStreamType() : null;
  }

  @Override
  public String getMessageCode() {
    return StreamType.isEvent(getStreamType()) ? "not.attending.event" : "not.attending.live.broadcast";
  }

  public static NotAttendingStreamResponse of() {
    return new NotAttendingStreamResponse();
  }
}
