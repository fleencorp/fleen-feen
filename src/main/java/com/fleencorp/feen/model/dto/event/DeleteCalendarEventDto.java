package com.fleencorp.feen.model.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCalendarEventDto {

  @NotNull(message = "{event.id.NotNull}")
  @IsNumber
  @JsonProperty("event_id")
  private String eventId;
}
