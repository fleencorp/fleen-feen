package com.fleencorp.feen.model.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCalendarDto {

  @NotNull(message = "{calendar.id.NotNull}")
  @JsonProperty("calendar_id")
  private Long calendarId;
}
