package com.fleencorp.feen.model.response.calendar;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "timezones",
  "countries"
})
public class DataForCreateCalendarResponse {

  @JsonProperty("timezones")
  private Set<String> timezones;

  @JsonProperty("countries")
  private List<?> countries;

  @Builder.Default
  private String message = "Data required for creating calendar retrieved successfully";

  public static DataForCreateCalendarResponse of(final Set<String> timezones, final List<?> countries) {
    return DataForCreateCalendarResponse.builder()
      .timezones(timezones)
      .countries(countries)
      .build();
  }
}
