package com.fleencorp.feen.shared.util;

public class SharedUtil {

  public static String sanitize(String input) {
    if (input == null) {
      return "";
    }

    return input.replaceAll("[^a-zA-Z0-9_]", "");
  }
}
