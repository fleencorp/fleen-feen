package com.fleencorp.feen.model.dto.stream.attendance;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.constant.stream.StreamType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessAttendeeRequestToJoinStreamDto {

  @NotNull(message = "{stream.attendeeUserId.NotNull}")
  @IsNumber
  @JsonProperty("attendee_user_id")
  private String attendeeUserId;

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

  private StreamType getStreamType() {
    return StreamType.of(streamType);
  }

  public boolean isEvent() {
    return StreamType.isEvent(getStreamType());
  }

  public StreamAttendeeRequestToJoinStatus getActualJoinStatus() {
    return StreamAttendeeRequestToJoinStatus.of(joinStatus);
  }

  /**
   * Checks if the attendee's request to join status is approved.
   *
   * @return {@code true} if the attendee's actual join status is {@link StreamAttendeeRequestToJoinStatus#APPROVED},
   *         otherwise {@code false}.
   */
  public boolean isApproved() {
    return StreamAttendeeRequestToJoinStatus.isApproved(getActualJoinStatus());
  }
}
