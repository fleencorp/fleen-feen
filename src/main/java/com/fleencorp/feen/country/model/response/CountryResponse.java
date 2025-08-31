package com.fleencorp.feen.country.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "title",
  "code",
  "timezone",
  "created_on",
  "updated_on"
})
public class CountryResponse extends FleenFeenResponse {

  @JsonProperty("title")
  private String title;

  @JsonProperty("code")
  private String code;

  @JsonProperty("timezone")
  private String timezone;
}
