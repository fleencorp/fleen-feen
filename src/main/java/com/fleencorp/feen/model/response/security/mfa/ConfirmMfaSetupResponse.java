package com.fleencorp.feen.model.response.security.mfa;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message"
})
public class ConfirmMfaSetupResponse extends LocalizedResponse {

  @Override
  public String getMessageCode() {
    return "confirm.mfa.setup";
  }

  public static ConfirmMfaSetupResponse of() {
    return new ConfirmMfaSetupResponse();
  }
}
