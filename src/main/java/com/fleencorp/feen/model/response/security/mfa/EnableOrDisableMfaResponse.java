package com.fleencorp.feen.model.response.security.mfa;

import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class EnableOrDisableMfaResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "enable.disable.mfa";
  }

  public static EnableOrDisableMfaResponse of() {
    return new EnableOrDisableMfaResponse();
  }
}
