package com.fleencorp.feen.converter.impl.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class TextPlainJsonMessageConverter extends AbstractMessageConverter {

  private final ObjectMapper objectMapper;

  public TextPlainJsonMessageConverter(ObjectMapper objectMapper) {
    super(new MimeType("text", "plain", StandardCharsets.UTF_8));
    this.objectMapper = objectMapper;
  }

  @Override
  protected boolean supports(@NotNull Class<?> clazz) {
    return true;
  }

  /**
   * Converts the payload of an SQS message from a String to the specified target class using Jackson ObjectMapper.
   *
   * @param message       the message to convert
   * @param targetClass   the target class to convert the payload to
   * @param conversionHint an optional conversion hint
   * @return the converted object, or null if conversion fails
   */
  @Override
  protected Object convertFromInternal(Message<?> message, @NotNull Class<?> targetClass, Object conversionHint) {
    if (message.getPayload() instanceof String payload) {
      try {
        return objectMapper.readValue(payload, targetClass);
      } catch (IOException e) {
        log.error("Unable to convert message", e);
        return null;
      }
    }
    return null;
  }
}
