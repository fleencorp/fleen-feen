package com.fleencorp.feen.mfa.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
  "message",
  "status"
})
public class EnableOrDisableMfaResponse extends LocalizedResponse {

  @JsonProperty("status")
  private boolean status;

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "enable.disable.mfa";
  }

  public static EnableOrDisableMfaResponse of(final boolean status) {
    return new EnableOrDisableMfaResponse(status);
  }
}
