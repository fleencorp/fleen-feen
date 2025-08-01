package com.fleencorp.feen.stream.model.response.common.live.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
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
public class DataForCreateLiveBroadcastResponse extends LocalizedResponse {

  @JsonProperty("timezones")
  private Set<String> timezones;

  @JsonProperty("categories")
  private List<?> categories;

  @Override
  public String getMessageCode() {
    return "data.for.create.stream";
  }

  public static DataForCreateLiveBroadcastResponse of(final Set<String> timezones, final List<?> categories) {
    return new DataForCreateLiveBroadcastResponse(timezones, categories);
  }
}
