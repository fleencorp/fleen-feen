package com.fleencorp.feen.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleencorp.feen.exception.base.FleenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>Utility class for converting Java objects to JSON strings and vice versa.
 * This class is annotated with Spring's {@link Component} annotation to indicate
 * that it is a Spring-managed component and can be injected into other Spring beans.
 * </p><br/>
 *
 * <p>
 * The {@link JsonUtil} class contains a single method, {@code convertToString},
 * which takes an object as input and converts it to a JSON string using the provided
 * {@link ObjectMapper}. If any error occurs during the conversion process,
 * a {@link RuntimeException} is thrown with an appropriate error message.
 * </p>
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
  public JsonUtil(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Converts the specified object to a JSON string.
   *
   * @param value the object to be converted to JSON
   * @return the JSON string representation of the object
   * @throws FleenException if an error occurs during the conversion process
   */
  public String convertToString(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (Exception ex) {
      String message = String.format("Convert to String failed, %s", ex.getMessage());
      throw new FleenException(message);
    }
  }

}
