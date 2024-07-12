package com.fleencorp.feen.model.request.verification;

import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.model.request.auth.ProfileRequest;
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
public class ResendVerificationCodeRequest extends ProfileRequest {

  protected String verificationCode;
  protected VerificationType verificationType;
}
