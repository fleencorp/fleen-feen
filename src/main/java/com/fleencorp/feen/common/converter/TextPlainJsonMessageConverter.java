package com.fleencorp.feen.common.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.fleencorp.feen.common.util.LoggingUtil.logIfEnabled;

/**
 * A custom message converter that converts messages with "text/plain" MIME type to JSON objects using {@link ObjectMapper}.
 * This converter extends {@link AbstractMessageConverter} and uses UTF-8 encoding.
 *
 * @author Yusuf ALamu Musa
 * @version 1.0
 */
@Slf4j
public class TextPlainJsonMessageConverter extends AbstractMessageConverter {

  private final ObjectMapper objectMapper;

  /**
   * Constructs a {@link TextPlainJsonMessageConverter} with the specified {@link ObjectMapper}.
   *
   * @param objectMapper the {@link ObjectMapper} used for JSON serialization and deserialization.
   */
  public TextPlainJsonMessageConverter(final ObjectMapper objectMapper) {
    super(new MimeType("text", "plain", StandardCharsets.UTF_8));
    this.objectMapper = objectMapper;
  }

  /**
   * Determines whether this converter can convert the specified class type.
   * This implementation returns {@code true} for all classes.
   *
   * @param clazz the class type to check for conversion support.
   * @return {@code true} indicating that this converter supports all class types.
   */
  @Override
  protected boolean supports(@NotNull final Class<?> clazz) {
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
  protected Object convertFromInternal(final Message<?> message, @NotNull final Class<?> targetClass, final Object conversionHint) {
    if (message.getPayload() instanceof final String payload) {
      try {
        return objectMapper.readValue(payload, targetClass);
      } catch (final IOException ex) {
        logIfEnabled(log::isErrorEnabled, () -> log.error("Unable to convert message. Reason: {}", ex.getMessage()));
        return null;
      }
    }
    return null;
  }
}
