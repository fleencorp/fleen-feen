package com.fleencorp.feen.model.request.profile;

import com.fleencorp.feen.model.request.verification.SendVerificationCodeRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.constant.message.CommonMessageDetails.PROFILE_UPDATE_VERIFICATION;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateVerificationRequest extends SendVerificationCodeRequest {

  @Override
  public String getTemplateName() {
    return PROFILE_UPDATE_VERIFICATION.getTemplateName();
  }

  @Override
  public String getMessageTitle() {
    return PROFILE_UPDATE_VERIFICATION.getMessageTitle();
  }
}
