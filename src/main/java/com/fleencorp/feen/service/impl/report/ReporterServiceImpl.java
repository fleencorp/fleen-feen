package com.fleencorp.feen.service.impl.report;

import com.fleencorp.feen.constant.base.ReportMessageType;
import com.fleencorp.feen.service.report.ReporterService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * {@code ReporterServiceImpl} is an implementation of the {@code ReporterService} interface.
 * This class provides methods for sending messages, typically for reporting or logging purposes.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
@Primary
public class ReporterServiceImpl implements ReporterService {

  private final ReporterService reporterService;

  /**
   * Constructs a new instance of {@code ReporterServiceImpl}.
   *
   * @param reporterService the {@code ReporterService} to be used for sending reports, qualified with "slack"
   */
  public ReporterServiceImpl(
    @Qualifier("slack") ReporterService reporterService) {
    this.reporterService = reporterService;
  }

  /**
   * Sends a message with a specific report message type using the {@code ReporterService}.
   *
   * @param message           the message to be sent
   * @param reportMessageType the type of the report message
   */
  @Override
  @Async
  public void sendMessage(String message, ReportMessageType reportMessageType) {
    reporterService.sendMessage(message, reportMessageType);
  }

  /**
   * Asynchronously sends a message using the {@code ReporterService}.
   *
   * @param message the message to be sent
   */
  @Override
  @Async
  public void sendMessage(String message) {
    reporterService.sendMessage(message);
  }
}
