package com.fleencorp.feen.event.service;

import com.fleencorp.feen.event.model.base.PublishMessageRequest;
import org.springframework.scheduling.annotation.Async;

public interface PublisherService {

  @Async
  void publishMessage(PublishMessageRequest messageRequest);
}
