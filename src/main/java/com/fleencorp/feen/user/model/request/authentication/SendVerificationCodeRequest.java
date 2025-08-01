package com.fleencorp.feen.user.model.request.authentication;

import com.fleencorp.feen.common.constant.message.MessageRequestType;
import com.fleencorp.feen.chat.space.model.request.external.message.MessageRequest;
import com.fleencorp.feen.verification.constant.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.fleencorp.feen.common.constant.message.MessageTemplateField.VERIFICATION_CODE;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendVerificationCodeRequest extends MessageRequest {

  protected String verificationCode;
  protected VerificationType verificationType;

  @Override
  public Map<String, Object> toMessagePayload() {
    final Map<String, Object> payload = new HashMap<>(super.toMessagePayload());
    payload.put(VERIFICATION_CODE.getValue(), verificationCode);

    return payload;
  }

  @Override
  public MessageRequestType getRequestType() {
    return null;
  }

  @Override
  public String getTemplateName() {
    return "";
  }

  @Override
  public String getMessageTitle() {
    return "";
  }
}
