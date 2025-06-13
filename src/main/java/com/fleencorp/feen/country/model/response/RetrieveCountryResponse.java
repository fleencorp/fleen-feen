package com.fleencorp.feen.country.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "message",
  "country"
})
public class RetrieveCountryResponse extends LocalizedResponse {

  @JsonProperty("country")
  private CountryResponse country;

  @Override
  public String getMessageCode() {
    return "retrieve.country";
  }

  public static RetrieveCountryResponse of(final CountryResponse country) {
    return new RetrieveCountryResponse(country);
  }
}
