package com.fleencorp.feen.model.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
public class DataForSignUpResponse {

  @JsonProperty("countries")
  private List<?> countries;

  @Builder.Default
  private String message = "Data required for creating sign up retrieved successfully";

  public static DataForSignUpResponse of(final List<?> countries) {
    return DataForSignUpResponse.builder()
      .countries(countries)
      .build();
  }
}