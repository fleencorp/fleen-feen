package com.fleencorp.feen.model.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.feen.constant.external.google.oauth2.Oauth2ServiceType;
import com.fleencorp.feen.converter.common.ToTitleCase;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.validator.CountryExist;
import com.fleencorp.feen.validator.TimezoneValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@ToString
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
  @JsonProperty("timezone")
  private String timezone;

  @NotBlank(message = "{calendar.country.NotBlank}")
  @Size(max = 1000, message = "{calendar.country.Size}")
  @CountryExist
  @JsonProperty("country_code")
  private String countryCode;

  public Calendar toCalendar() {
    return Calendar.builder()
            .title(title)
            .description(description)
            .timezone(timezone)
            .isActive(true)
            .code(countryCode)
            .build();
  }

  public Oauth2ServiceType getOauth2ServiceType() {
    return Oauth2ServiceType.GOOGLE_CALENDAR;
  }
}
