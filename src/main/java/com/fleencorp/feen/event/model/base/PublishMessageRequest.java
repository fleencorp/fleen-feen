package com.fleencorp.feen.event.model.base;

import com.fleencorp.feen.constant.queue.MessageType;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublishMessageRequest {

  private Object message;
  private MessageType messageType;

  public static PublishMessageRequest of(Object message) {
    return PublishMessageRequest.builder()
        .message(message)
        .build();
  }
}
