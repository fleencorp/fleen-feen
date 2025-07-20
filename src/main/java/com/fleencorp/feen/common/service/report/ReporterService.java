package com.fleencorp.feen.common.service.report;

import com.fleencorp.feen.common.constant.base.ReportMessageType;
import org.springframework.scheduling.annotation.Async;

public interface ReporterService {

  @Async
  void sendMessage(String message, ReportMessageType reportMessageType);

  @Async
  void sendMessage(String message);
}
