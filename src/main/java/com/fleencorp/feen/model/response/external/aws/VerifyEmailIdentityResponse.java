package com.fleencorp.feen.model.response.external.aws;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message"
})
public class VerifyEmailIdentityResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "verify.email.identity.response";
  }

  public static VerifyEmailIdentityResponse of() {
    return new VerifyEmailIdentityResponse();
  }
}