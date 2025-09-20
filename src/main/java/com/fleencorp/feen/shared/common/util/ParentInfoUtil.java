package com.fleencorp.feen.shared.common.util;

import static java.util.Objects.nonNull;

public final class ParentInfoUtil {

  private ParentInfoUtil() {}

  public static String getParentSummary(final String content) {
    return truncate(content, 200);
  }

  public static String getSoftAskTitle(final String title) {
    return truncate(title, 200);
  }

  public static String truncate(final String value, final int maxLength) {
    if (nonNull(value) && value.trim().length() > maxLength) {
      return value.substring(0, maxLength);
    } else {
      return value;
    }
  }
}
