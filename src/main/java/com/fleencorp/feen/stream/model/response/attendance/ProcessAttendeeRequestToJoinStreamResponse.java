package com.fleencorp.feen.stream.model.response.attendance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.model.info.attendance.AttendanceInfo;
import com.fleencorp.feen.stream.model.info.attendance.AttendeeCountInfo;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
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
  "attendee_count_info",
  "stream"
})
public class ProcessAttendeeRequestToJoinStreamResponse extends LocalizedResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("attendance_info")
  private AttendanceInfo attendanceInfo;

  @JsonProperty("stream_type_info")
  protected StreamTypeInfo streamTypeInfo;

  @JsonProperty("attendee_count_info")
  private AttendeeCountInfo attendeeCountInfo;

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

  public static ProcessAttendeeRequestToJoinStreamResponse of(final Long streamId, final AttendanceInfo attendanceInfo, final StreamTypeInfo streamTypeInfo, final AttendeeCountInfo attendeeCountInfo) {
    return new ProcessAttendeeRequestToJoinStreamResponse(streamId, attendanceInfo, streamTypeInfo, attendeeCountInfo);
  }
}
