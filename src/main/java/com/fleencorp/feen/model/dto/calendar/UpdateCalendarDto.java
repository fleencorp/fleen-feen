package com.fleencorp.feen.model.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCalendarDto extends CreateCalendarDto {

  @NotNull(message = "{calendar.id.NotNull}")
  @IsNumber
  @JsonProperty("calendar_id")
  private String calendarId;
}
