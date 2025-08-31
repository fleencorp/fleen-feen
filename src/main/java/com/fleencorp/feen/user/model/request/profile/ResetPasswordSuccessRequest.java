package com.fleencorp.feen.user.model.request.profile;

import com.fleencorp.feen.chat.space.model.request.external.message.MessageRequest;
import com.fleencorp.feen.common.constant.message.MessageRequestType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fleencorp.feen.common.constant.message.CommonMessageDetails.RESET_PASSWORD_SUCCESS;

@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordSuccessRequest extends MessageRequest {

  @Override
  public MessageRequestType getRequestType() {
    return MessageRequestType.RESET_PASSWORD_SUCCESS;
  }

  @Override
  public String getTemplateName() {
    return RESET_PASSWORD_SUCCESS.getTemplateName();
  }

  @Override
  public String getMessageTitle() {
    return RESET_PASSWORD_SUCCESS.getMessageTitle();
  }
}
