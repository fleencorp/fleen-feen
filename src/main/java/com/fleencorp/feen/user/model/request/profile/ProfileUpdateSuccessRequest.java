package com.fleencorp.feen.user.model.request.profile;

import com.fleencorp.feen.common.constant.message.MessageRequestType;
import com.fleencorp.feen.chat.space.model.request.external.message.MessageRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.common.constant.message.CommonMessageDetails.PROFILE_UPDATE_SUCCESS;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateSuccessRequest extends MessageRequest {

  @Override
  public MessageRequestType getRequestType() {
    return MessageRequestType.PROFILE_UPDATE_SUCCESS;
  }

  @Override
  public String getTemplateName() {
    return PROFILE_UPDATE_SUCCESS.getTemplateName();
  }

  @Override
  public String getMessageTitle() {
    return PROFILE_UPDATE_SUCCESS.getMessageTitle();
  }
}
