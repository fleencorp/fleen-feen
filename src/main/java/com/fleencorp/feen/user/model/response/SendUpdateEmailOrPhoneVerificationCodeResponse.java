package com.fleencorp.feen.user.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SendUpdateEmailOrPhoneVerificationCodeResponse extends LocalizedResponse {

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "send.update.email.or.phone.verification.code";
  }

  public static SendUpdateEmailOrPhoneVerificationCodeResponse of() {
    return new SendUpdateEmailOrPhoneVerificationCodeResponse();
  }
}
