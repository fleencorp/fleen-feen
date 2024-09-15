package com.fleencorp.feen.model.response.country;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "message",
  "country"
})
public class RetrieveCountryResponse extends ApiResponse {

  @JsonProperty("country")
  private CountryResponse country;

  @Override
  public String getMessageCode() {
    return "retrieve.country";
  }

  public static RetrieveCountryResponse of(final CountryResponse country) {
    return RetrieveCountryResponse.builder()
      .country(country)
      .build();
  }
}
