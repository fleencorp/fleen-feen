package com.fleencorp.feen.model.response.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
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
public class DataForCreateStreamResponse extends ApiResponse {

  @JsonProperty("timezones")
  private Set<String> timezones;

  @JsonProperty("categories")
  private List<?> categories;

  @Override
  public String getMessageCode() {
    return "data.for.create.stream";
  }

  public static DataForCreateStreamResponse of(final Set<String> timezones, final List<?> categories) {
    return DataForCreateStreamResponse.builder()
      .timezones(timezones)
      .categories(categories)
      .build();
  }
}
