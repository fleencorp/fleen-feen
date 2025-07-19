package com.fleencorp.feen.softask.model.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id"
})
public class SoftAskContentUpdateResponse extends LocalizedResponse {

  @JsonProperty("id")
  private Long id;

  @Override
  public String getMessageCode() {
    return "soft.ask.content.update";
  }

  public static SoftAskContentUpdateResponse of(final Long id) {
    return new SoftAskContentUpdateResponse(id);
  }
}
