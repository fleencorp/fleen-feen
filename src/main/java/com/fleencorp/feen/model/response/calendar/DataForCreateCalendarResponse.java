package com.fleencorp.feen.model.response.calendar;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.ApiResponse;
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
public class DataForCreateCalendarResponse extends ApiResponse {

  @JsonProperty("timezones")
  private Set<String> timezones;

  @JsonProperty("countries")
  private List<?> countries;

  @Override
  public String getMessageCode() {
    return "data.for.create.calendar";
  }

  public static DataForCreateCalendarResponse of(final Set<String> timezones, final List<?> countries) {
    return DataForCreateCalendarResponse.builder()
      .timezones(timezones)
      .countries(countries)
      .build();
  }
}
