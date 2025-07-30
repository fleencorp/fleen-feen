package com.fleencorp.feen.common.event.model.base;

import com.fleencorp.feen.common.constant.message.MessageRequestType;
import com.fleencorp.feen.chat.space.model.request.external.message.MessageRequest;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublishMessageRequest {

  private MessageRequest message;
  private MessageRequestType messageType;

  public static PublishMessageRequest of(final MessageRequest message) {
    return PublishMessageRequest.builder()
        .message(message)
        .build();
  }
}
