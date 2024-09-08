package com.fleencorp.feen.model.request.profile;

import com.fleencorp.feen.constant.message.MessageRequestType;
import com.fleencorp.feen.model.request.message.MessageRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.constant.message.CommonMessageDetails.PROFILE_UPDATE_SUCCESS;

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
