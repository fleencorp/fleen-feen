package com.fleencorp.feen.model.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddNewEventAttendeeDto {

  @NotNull(message = "{event.id.NotNull}")
  @IsNumber
  @JsonProperty("event_id")
  private String eventId;

  @NotNull(message = "{event.userId.NotNull}")
  @IsNumber
  @JsonProperty("user_id")
  private String userId;

  @NotBlank
  @Size
  @JsonProperty("alias_or_display_name")
  private String aliasOrDisplayName;
}
