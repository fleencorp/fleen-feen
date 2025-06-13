package com.fleencorp.feen.calendar.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToTitleCase;
import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.constant.external.google.oauth2.Oauth2ServiceType;
import com.fleencorp.feen.validator.TimezoneValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
  @JsonProperty("timezone")
  private String timezone;

  @NotBlank(message = "{calendar.country.NotBlank}")
  @Size(max = 1000, message = "{calendar.country.Size}")
  @JsonProperty("country_code")
  private String countryCode;

  public Calendar toCalendar() {
    final Calendar calendar = new Calendar();
    calendar.setTitle(title);
    calendar.setDescription(description);
    calendar.setTimezone(timezone);
    calendar.setCode(countryCode);
    calendar.setIsActive(true);

    return calendar;
  }

  public Oauth2ServiceType getOauth2ServiceType() {
    return Oauth2ServiceType.googleCalendar();
  }
}
