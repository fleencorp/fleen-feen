package com.fleencorp.feen.model.request.profile;

import com.fleencorp.feen.model.request.message.MessageRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.constant.message.CommonMessageDetails.RESET_PASSWORD_SUCCESSFUL;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordSuccessRequest extends MessageRequest {

  @Override
  public String getTemplateName() {
    return RESET_PASSWORD_SUCCESSFUL.getTemplateName();
  }

  @Override
  public String getMessageTitle() {
    return RESET_PASSWORD_SUCCESSFUL.getMessageTitle();
  }
}
