package com.fleencorp.feen.model.request.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.fleencorp.feen.constant.message.MessageTemplateField.*;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

  protected String firstName;
  protected String lastName;
  protected String emailAddress;
  protected String phoneNumber;
  protected String emailMessageBody;
  protected Object smsMessage;
  protected String errorMessage;

  public Map<String, Object> toMessagePayload() {
    Map<String, Object> payload = new HashMap<>();
    payload.put(FIRST_NAME.getValue(), firstName);
    payload.put(LAST_NAME.getValue(), lastName);
    payload.put(EMAIL_ADDRESS.getValue(), emailAddress);
    payload.put(PHONE_NUMBER.getValue(), phoneNumber);

    return payload;
  }

  public String getTemplateName() {
    return null;
  }

  public String getMessageTitle() {
    return null;
  }
}
