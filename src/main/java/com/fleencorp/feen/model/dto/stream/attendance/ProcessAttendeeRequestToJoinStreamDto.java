package com.fleencorp.feen.model.dto.stream.attendance;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessAttendeeRequestToJoinStreamDto {

  @NotNull(message = "{event.attendeeUserId.NotNull}")
  @IsNumber
  @JsonProperty("attendee_user_id")
  private String attendeeUserId;

  @NotNull(message = "{event.joinStatus.NotNull}")
  @OneOf(enumClass = StreamAttendeeRequestToJoinStatus.class, message = "{event.joinStatus.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("join_status")
  private String joinStatus;

  @Size(min = 10, max = 500, message = "{event.comment.Size}")
  @JsonProperty("comment")
  protected String comment;

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
