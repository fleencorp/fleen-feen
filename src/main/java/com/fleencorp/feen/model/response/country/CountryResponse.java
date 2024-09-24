package com.fleencorp.feen.model.response.country;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "title",
  "code",
  "timezone",
  "created_on",
  "updated_on",
  "message"
})
public class CountryResponse extends FleenFeenResponse {

  @JsonProperty("title")
  private String title;

  @JsonProperty("code")
  private String code;

  @JsonProperty("timezone")
  private String timezone;
}
