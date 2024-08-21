package com.fleencorp.feen.model.response.security.mfa;

import com.fleencorp.feen.model.response.base.ApiResponse;
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
  public String getMessageKey() {
    return "enable.disable.mfa";
  }

  public static EnableOrDisableMfaResponse of() {
    return new EnableOrDisableMfaResponse();
  }
}
