package com.fleencorp.feen.model.response.user.profile;

import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class SendUpdateEmailOrPhoneVerificationCodeResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "send.update.email.or.phone.verification.code";
  }

  public static SendUpdateEmailOrPhoneVerificationCodeResponse of() {
    return new SendUpdateEmailOrPhoneVerificationCodeResponse();
  }
}
