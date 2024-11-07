package com.fleencorp.feen.model.response.security.mfa;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "status"
})
public class EnableOrDisableMfaResponse extends ApiResponse {

  @JsonProperty("status")
  private boolean status;

  @Override
  public String getMessageCode() {
    return "enable.disable.mfa";
  }

  public static EnableOrDisableMfaResponse of(final boolean status) {
    return new EnableOrDisableMfaResponse(status);
  }
}
