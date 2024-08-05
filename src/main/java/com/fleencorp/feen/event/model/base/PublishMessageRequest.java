package com.fleencorp.feen.event.model.base;

import com.fleencorp.feen.model.request.message.MessageRequest;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublishMessageRequest {

  private Object message;
  private MessageRequest messageType;

  public static PublishMessageRequest of(final Object message) {
    return PublishMessageRequest.builder()
        .message(message)
        .build();
  }
}
