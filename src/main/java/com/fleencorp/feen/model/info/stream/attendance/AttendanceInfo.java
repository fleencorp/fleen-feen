package com.fleencorp.feen.model.info.stream.attendance;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsASpeakerInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsAttendingInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "request_to_join_status_info",
  "join_status_info",
  "is_attending_info",
  "is_a_speaker_info"
})
public class AttendanceInfo {

  @JsonProperty("request_to_join_status_info")
  private StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo;

  @JsonProperty("join_status_info")
  private JoinStatusInfo joinStatusInfo;

  @JsonProperty("is_attending_info")
  private IsAttendingInfo attendingInfo;

  @JsonProperty("is_a_speaker_info")
  private IsASpeakerInfo isASpeaker;

  public static AttendanceInfo of(final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo, final JoinStatusInfo joinStatusInfo, final IsAttendingInfo isAttendingInfo, final IsASpeakerInfo isASpeaker) {
    return new AttendanceInfo(requestToJoinStatusInfo, joinStatusInfo, isAttendingInfo, isASpeaker);
  }

  public static AttendanceInfo of() {
    final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = new StreamAttendeeRequestToJoinStatusInfo();
    final JoinStatusInfo joinStatusInfo = JoinStatusInfo.of();
    final IsAttendingInfo isAttendingInfo = IsAttendingInfo.of();
    final IsASpeakerInfo isASpeaker = IsASpeakerInfo.of();

    return of(requestToJoinStatusInfo, joinStatusInfo, isAttendingInfo, isASpeaker);
  }

}
