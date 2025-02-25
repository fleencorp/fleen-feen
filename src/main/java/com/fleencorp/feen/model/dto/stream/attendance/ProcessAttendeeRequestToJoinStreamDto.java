package com.fleencorp.feen.model.dto.stream.attendance;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessAttendeeRequestToJoinStreamDto {

  @NotNull(message = "{stream.attendeeId.NotNull}")
  @IsNumber
  @JsonProperty("attendee_id")
  private String attendeeId;

  @NotNull(message = "{stream.joinStatus.NotNull}")
  @OneOf(enumClass = StreamAttendeeRequestToJoinStatus.class, message = "{stream.joinStatus.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("join_status")
  private String joinStatus;

  @Size(min = 10, max = 500, message = "{stream.comment.Size}")
  @JsonProperty("comment")
  protected String comment;

  @NotNull(message = "{stream.streamType.NotNull}")
  @OneOf(enumClass = StreamType.class, message = "{stream.streamType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("stream_type")
  protected String streamType;

  public StreamType getStreamType() {
    return StreamType.of(streamType);
  }

  public boolean isEvent() {
    return StreamType.isEvent(getStreamType());
  }

  public Long getAttendeeId() {
    return Long.parseLong(attendeeId);
  }

  public StreamAttendeeRequestToJoinStatus getJoinStatus() {
    return StreamAttendeeRequestToJoinStatus.of(joinStatus);
  }

  /**
   * Checks if the attendee's request to join status is approved.
   *
   * @return {@code true} if the attendee's join status is {@link StreamAttendeeRequestToJoinStatus#APPROVED},
   *         otherwise {@code false}.
   */
  public boolean isApproved() {
    return StreamAttendeeRequestToJoinStatus.isApproved(getJoinStatus());
  }
}
