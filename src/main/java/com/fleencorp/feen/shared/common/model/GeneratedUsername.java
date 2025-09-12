package com.fleencorp.feen.shared.common.model;

import com.fleencorp.base.converter.impl.common.ToTitleCaseConverter;

import static java.util.Objects.nonNull;

public record GeneratedUsername(String username, String displayName, String displayName2) {

  public static GeneratedUsername of(final String username, final String displayName, final String displayName2) {
    return new GeneratedUsername(username, displayName, displayName2);
  }

  public static GeneratedUsername of(final String username, final String displayName) {
    return new GeneratedUsername(username, displayName, null);
  }

  public static String createDisplayName(final String word1, final String word2) {
    return ToTitleCaseConverter.toTitleCase(word1) + ' ' + ToTitleCaseConverter.toTitleCase(word2);
  }

  public static String createOtherDisplayName(final String word1, final String word2, final int other) {
    return createDisplayName(word1, word2) + ' ' + other;
  }

  public static String createCacheValue(final String username, final String displayName) {
    return username + ":::" + displayName;
  }

  public static GeneratedUsername getFromCachedValue(String username) {
    String displayName = username;

    if (nonNull(username)) {
      final String[] names = username.split(":::");

      if (names.length == 2) {
        username = names[0];
        displayName = names[1];
      }
    }

    return GeneratedUsername.of(username, displayName);
  }
}
