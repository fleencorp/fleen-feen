package com.fleencorp.feen.model.response.stream.attendance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.response.holder.TryToJoinPrivateOrProtectedStreamResponse;
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
  "stream_type_info"
})
public class RequestToJoinStreamResponse extends ApiResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("attendance_info")
  private AttendanceInfo attendanceInfo;

  @JsonProperty("stream_type_info")
  private StreamTypeInfo streamTypeInfo;

  @JsonIgnore
  private TryToJoinPrivateOrProtectedStreamResponse tryToJoinResponse;

  @JsonIgnore
  protected StreamType getStreamType() {
    return nonNull(streamTypeInfo) ? streamTypeInfo.getStreamType() : null;
  }

  @Override
  public String getMessageCode() {
    return StreamType.isEvent(getStreamType()) ? "request.to.join.event" : "request.to.join.live.broadcast";
  }

  public static RequestToJoinStreamResponse of(final Long streamId, final AttendanceInfo attendanceInfo, final StreamTypeInfo streamTypeInfo, final TryToJoinPrivateOrProtectedStreamResponse tryToJoinResponse) {
    return new RequestToJoinStreamResponse(streamId, attendanceInfo, streamTypeInfo, tryToJoinResponse);
  }
}