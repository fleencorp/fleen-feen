package com.fleencorp.feen.model.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "countries"
})
public class DataForSignUpResponse extends ApiResponse {

  @JsonProperty("countries")
  private List<?> countries;

  @Override
  public String getMessageCode() {
    return "data.for.sign.up";
  }

  public static DataForSignUpResponse of(final List<?> countries) {
    return DataForSignUpResponse.builder()
      .countries(countries)
      .build();
  }
}
