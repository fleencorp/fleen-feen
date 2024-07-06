package com.fleencorp.feen.model.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.converter.ToLowerCase;
import com.fleencorp.feen.converter.ToTitleCase;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.validator.CountryExist;
import com.fleencorp.feen.validator.TimezoneValid;
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
  @Size(min = 10, max = 300, message = "{calendar.title.Size}")
  @ToTitleCase
  @JsonProperty("title")
  private String title;

  @NotBlank(message = "{calendar.description.NotBlank}")
  @Size(max = 1000, message = "{calendar.description.Size}")
  @JsonProperty("description")
  private String description;

  @NotBlank(message = "{calendar.timezone.NotBlank}")
  @Size(max = 30, message = "{calendar.timezone.Size}")
  @TimezoneValid
  @ToLowerCase
  @JsonProperty("timezone")
  private String timezone;

  @NotBlank(message = "{calendar.country.NotBlank}")
  @IsNumber
  @CountryExist
  @JsonProperty("country")
  private String country;

  public Calendar toCalendar() {
    return Calendar.builder()
            .title(title)
            .description(description)
            .timezone(timezone)
            .isActive(true)
            .build();
  }
}
