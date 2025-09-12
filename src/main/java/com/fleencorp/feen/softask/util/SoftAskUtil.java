package com.fleencorp.feen.softask.util;

import static java.util.Objects.nonNull;

public final class SoftAskUtil {

  private SoftAskUtil() {}

  public static String getParentSummary(final String content) {
    if (nonNull(content) && content.trim().length() > 200) {
      return content.substring(0, 200);
    } else {
      return content;
    }
  }
}
