package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.*;

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
public class DataForCreateEventResponse extends ApiResponse {

  @JsonProperty("timezones")
  private Set<String> timezones;

  @Override
  public String getMessageCode() {
    return "data.for.create.event";
  }

  public static DataForCreateEventResponse of(final Set<String> timezones) {
    return DataForCreateEventResponse.builder()
      .timezones(timezones)
      .build();
  }
}
