package com.fleencorp.feen.stream.model.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "timezones"
})
public class DataForRescheduleStreamResponse extends LocalizedResponse {

  @JsonProperty("timezones")
  private Set<String> timezones;

  @Override
  public String getMessageCode() {
    return "data.for.reschedule.stream";
  }

  public static DataForRescheduleStreamResponse of(final Set<String> timezones) {
    return new DataForRescheduleStreamResponse(timezones);
  }
}
