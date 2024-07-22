package com.fleencorp.feen.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleencorp.feen.exception.base.FleenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

/**
 * <p>Utility class for converting Java objects to JSON strings and vice versa.
 * This class is annotated with Spring's {@link Component} annotation to indicate
 * that it is a Spring-managed component and can be injected into other Spring beans.
 * </p>
 *
 * <p>The {@link JsonUtil} class contains a single method, {@code convertToString},
 * which takes an object as input and converts it to a JSON string using the provided
 * {@link ObjectMapper}. If any error occurs during the conversion process,
 * a {@link RuntimeException} is thrown with an appropriate error message.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
@Slf4j
public class JsonUtil {

  private final ObjectMapper objectMapper;

  /**
   * Constructs a new {@code JsonUtil} instance with the specified {@link ObjectMapper}.
   *
   * @param objectMapper the {@link ObjectMapper} used for JSON conversion
   */
  public JsonUtil(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Converts the specified object to a JSON string.
   *
   * @param value the object to be converted to JSON
   * @return the JSON string representation of the object
   * @throws FleenException if an error occurs during the conversion process
   */
  public String convertToString(final Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (final Exception ex) {
      final String message = String.format("Convert to String failed, %s", ex.getMessage());
      throw new FleenException(message);
    }
  }

  /**
   * Retrieves the value associated with the specified key from the cache and deserializes it into an object of the given class.
   *
   * @param value  The value to deserialize for.
   * @param clazz The class type of the value to deserialize.
   * @param <T>   The type of the value to deserialize.
   * @return The deserialized value associated with the key,
   * or null if the value does not exist or the value could not be deserialized.
   */
  public <T> T get(String value, Class<T> clazz) {
    if (nonNull(value) && nonNull(clazz)) {
      try {
        return objectMapper.readValue(value, clazz);
      } catch (JsonProcessingException ex) {
        log.error(ex.getMessage(), ex);
      }
    }
    return null;
  }

}
