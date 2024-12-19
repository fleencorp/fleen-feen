package com.fleencorp.feen.repository.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleencorp.feen.model.message.SmsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.fleencorp.base.util.FleenUtil.readResourceFile;
import static com.fleencorp.feen.util.LoggingUtil.logIfEnabled;

/**
 * Repository for managing SMS message templates.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Component
public class SmsMessageRepository {

  private final String messageTemplatePath;
  private final ObjectMapper objectMapper;
  private List<SmsMessage> smsMessages;

  /**
   * Constructs an SmsMessageRepository with the specified message template path,
   * ObjectMapper for JSON parsing, and an initial list of SMS messages.
   *
   * @param messageTemplatePath The path to the SMS message templates.
   * @param objectMapper        ObjectMapper instance for JSON deserialization.
   */
  public SmsMessageRepository(
      @Value("${sms.message.templates-path}") final String messageTemplatePath,
      final ObjectMapper objectMapper) {
    this.messageTemplatePath = messageTemplatePath;
    this.objectMapper = objectMapper;
    this.smsMessages = new ArrayList<>();
  }

  /**
   * Retrieves a list of SMS messages. If the list is already populated, returns it directly.
   * Otherwise, reads the SMS message templates from a resource file and parses them into objects.
   *
   * @return A list of {@link SmsMessage} objects containing SMS templates, or an empty list if parsing fails.
   */
  protected List<SmsMessage> getSmsMessages() {
    if (!smsMessages.isEmpty()) {
      return smsMessages;
    }

    final String value = readResourceFile(messageTemplatePath);
    try {
      smsMessages = objectMapper.readValue(value, new TypeReference<>() {});
      return smsMessages;
    } catch (final JsonProcessingException ex) {
      logIfEnabled(log::isErrorEnabled, () -> log.error(ex.getMessage(), ex));
    }
    return List.of();
  }

  /**
   * Finds an {@link SmsMessage} by its title.
   *
   * @param title The title of the SMS message to find.
   * @return An {@link Optional} containing the {@link SmsMessage} if found, or empty if not found.
   */
  public Optional<SmsMessage> findByTitle(final String title) {
    return getSmsMessages()
        .stream()
        .filter(message -> message.title().equalsIgnoreCase(title))
        .findFirst();
  }
}
