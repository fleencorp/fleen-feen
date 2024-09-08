package com.fleencorp.feen.service.report;

import com.fleencorp.feen.constant.base.ReportMessageType;
import org.springframework.scheduling.annotation.Async;

public interface ReporterService {

  @Async
  void sendMessage(String message, ReportMessageType reportMessageType);

  @Async
  void sendMessage(String message);
}
