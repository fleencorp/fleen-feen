package com.fleencorp.feen.service.report;

import com.fleencorp.feen.constant.base.MessageLevel;
import org.springframework.scheduling.annotation.Async;

public interface ReporterService {

  @Async
  void sendMessage(String groupOrChannel, String message);

  @Async
  void sendMessage(String groupOrChannelOrUrl, String message, MessageLevel messageLevel);
}
