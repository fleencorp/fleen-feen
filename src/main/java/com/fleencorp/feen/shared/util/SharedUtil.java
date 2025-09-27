package com.fleencorp.feen.shared.util;

public final class SharedUtil {

  private SharedUtil() {}

  public static String sanitize(String input) {
    if (input == null) {
      return "";
    }
    return input.replaceAll("\\W", "");
  }

}
