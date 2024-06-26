package com.fleencorp.feen.model.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInstantCalendarEventDto {

  @NotBlank(message = "{event.title.NotBlank}")
  @Size(min = 10, max = 500, message = "{event.title.Size}")
  @JsonProperty("title")
  private String title;
}
