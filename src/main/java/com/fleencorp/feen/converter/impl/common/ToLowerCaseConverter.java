package com.fleencorp.feen.converter.impl.common;

import com.fasterxml.jackson.databind.util.StdConverter;

/**
* This class represents a converter that converts a String to lowercase.
* It extends the StdConverter class provided by Jackson Databind library.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
public class ToLowerCaseConverter extends StdConverter<String, String> {

  /**
  * Converts the input String to lowercase.
  *
  * @param value The String to be converted to lowercase.
  * @return The lowercase version of the input String.
  */
  @Override
  public String convert(String value) {
    return value == null
            ? null
            : value.trim().toLowerCase();
  }
}