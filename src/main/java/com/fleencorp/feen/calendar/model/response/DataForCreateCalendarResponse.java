package com.fleencorp.feen.calendar.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.Set;

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
public class DataForCreateCalendarResponse extends LocalizedResponse {

  @JsonProperty("timezones")
  private Set<String> timezones;

  @JsonProperty("countries")
  private Collection<?> countries;

  @Override
  public String getMessageCode() {
    return "data.for.create.calendar";
  }

  public static DataForCreateCalendarResponse of(final Set<String> timezones, final Collection<?> countries) {
    return new DataForCreateCalendarResponse(timezones, countries);
  }
}
