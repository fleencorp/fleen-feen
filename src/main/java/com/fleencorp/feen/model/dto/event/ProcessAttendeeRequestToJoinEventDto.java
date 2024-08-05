package com.fleencorp.feen.model.dto.event;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.converter.common.ToUpperCase;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessAttendeeRequestToJoinEventDto {

  @NotNull(message = "{event.attendeeUserId.NotNull}")
  @IsNumber
  @JsonProperty("attendee_user_id")
  private String attendeeUserId;

  @NotNull(message = "{event.joinStatus.NotNull}")
  @ValidEnum(enumClass = StreamAttendeeRequestToJoinStatus.class, message = "{event.joinStatus.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("join_status")
  private String joinStatus;

  @Size(min = 10, max = 500, message = "{event.comment.Size}")
  @JsonProperty("comment")
  protected String comment;

  public StreamAttendeeRequestToJoinStatus getActualJoinStatus() {
    return parseEnumOrNull(joinStatus, StreamAttendeeRequestToJoinStatus.class);
  }
}
