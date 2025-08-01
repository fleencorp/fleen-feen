package com.fleencorp.feen.stream.model.response.common.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "timezones"
})
public class DataForCreateEventResponse extends LocalizedResponse {

  @JsonProperty("timezones")
  private Set<String> timezones;

  @Override
  public String getMessageCode() {
    return "data.for.create.event";
  }

  public static DataForCreateEventResponse of(final Set<String> timezones) {
    return new DataForCreateEventResponse(timezones);
  }
}
