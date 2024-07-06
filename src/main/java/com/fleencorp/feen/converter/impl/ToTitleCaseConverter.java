package com.fleencorp.feen.converter.impl;

import com.fasterxml.jackson.databind.util.StdConverter;

/**
* This class represents a converter that converts a String to title case.
* It extends the StdConverter class provided by Jackson Databind library.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
public class ToTitleCaseConverter extends StdConverter<String, String> {

  /**
  * Converts the input String to title case.
  *
  * @param value The String to be converted to title case.
  * @return The title case version of the input String.
  */
  @Override
  public String convert(final String value) {
    if (null == value) {
      return null;
    }
    return ToTitleCaseConverter.toTitleCase(value);
  }

  /**
  * Converts a given string to title case, where the first letter of each word is capitalized,
  * and the rest are in lowercase. Words are assumed to be separated by whitespace.
  *
  * @param value the string to be converted
  * @return the converted string in title case
  */
  public static String toTitleCase(final String value) {
    final String trimmedValue = value.trim();
    final StringBuilder titleCaseValue = new StringBuilder();
    boolean nextTitleCase = true;

    for (final char c : trimmedValue.toCharArray())
      if (Character.isWhitespace(c)) {
          nextTitleCase = true;
          titleCaseValue.append(c);
      } else if (nextTitleCase) {
          titleCaseValue.append(Character.toTitleCase(c));
          nextTitleCase = false;
      } else {
        titleCaseValue.append(Character.toLowerCase(c));
      }
    return titleCaseValue.toString();
  }
}
