package com.fleencorp.feen.stream.model.response.attendance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
import com.fleencorp.feen.stream.model.info.attendance.AttendanceInfo;
import com.fleencorp.localizer.model.response.LocalizedResponse;
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
  "attendance_info",
  "stream_type_info",
  "stream_link",
  "total_attending"
})
public class JoinStreamResponse extends LocalizedResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("attendance_info")
  private AttendanceInfo attendanceInfo;

  @JsonProperty("stream_type_info")
  private StreamTypeInfo streamTypeInfo;

  @JsonProperty("stream_link")
  private String streamLink;

  @JsonProperty("total_attending")
  private Integer totalAttending;

  @JsonIgnore
  protected StreamType getStreamType() {
    return nonNull(streamTypeInfo) ? streamTypeInfo.getStreamType() : null;
  }

  @Override
  public String getMessageCode() {
    return StreamType.isEvent(getStreamType()) ? "join.event" : "join.live.broadcast";
  }

  public static JoinStreamResponse of(final Long streamId, final AttendanceInfo attendanceInfo, final StreamTypeInfo streamTypeInfo, final String streamLink, final Integer totalAttending) {
    return new JoinStreamResponse(streamId, attendanceInfo, streamTypeInfo, streamLink, totalAttending);
  }
}
