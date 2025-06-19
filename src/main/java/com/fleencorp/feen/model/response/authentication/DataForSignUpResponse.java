package com.fleencorp.feen.model.response.authentication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "countries"
})
public class DataForSignUpResponse extends LocalizedResponse {

  @JsonProperty("countries")
  private Collection<?> countries;

  @Override
  public String getMessageCode() {
    return "data.for.sign.up";
  }

  public static DataForSignUpResponse of(final Collection<?> countries) {
    return new DataForSignUpResponse(countries);
  }
}
