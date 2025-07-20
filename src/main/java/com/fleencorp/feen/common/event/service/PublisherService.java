package com.fleencorp.feen.common.event.service;

import com.fleencorp.feen.common.event.model.base.PublishMessageRequest;
import org.springframework.scheduling.annotation.Async;

public interface PublisherService {

  @Async
  void publishMessage(PublishMessageRequest messageRequest);
}
