package com.fleencorp.feen.converter.impl.common;

import com.fasterxml.jackson.databind.util.StdConverter;

/**
 * This class represents a converter that converts a String to uppercase.
 * It extends the StdConverter class provided by Jackson Databind library.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public class ToUpperCaseConverter extends StdConverter<String, String> {

  /**
   * Converts the input String to uppercase.
   *
   * @param value The String to be converted to uppercase.
   * @return The uppercase version of the input String.
   */
  @Override
  public String convert(final String value) {
    return value == null
            ? null
            : value.trim().toUpperCase();
  }
}
