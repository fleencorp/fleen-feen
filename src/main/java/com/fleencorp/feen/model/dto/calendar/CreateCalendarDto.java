package com.fleencorp.feen.model.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class CreateCalendarDto {

  @NotBlank(message = "{calendar.title.NotBlank}")
  @Size(max = 50, message = "{calendar.title.Size}")
  @JsonProperty("title")
  private String title;

  @NotBlank(message = "{calendar.description.NotBlank}")
  @Size(max = 500, message = "{calendar.description.Size}")
  @JsonProperty("description")
  private String description;

  @NotBlank(message = "{calendar.timezone.NotBlank}")
  @Size(max = 30, message = "{calendar.timezone.Size}")
  @JsonProperty("timezone")
  private String timezone;
}
