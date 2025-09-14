package com.fleencorp.feen.shared.common.model;

import com.fleencorp.base.converter.impl.common.ToTitleCaseConverter;

import static java.util.Objects.nonNull;

public record GeneratedParticipantDetail(String username, String displayName, String displayName2, String avatar) {

  public static GeneratedParticipantDetail of(final String username, final String displayName, final String displayName2, final String avatar) {
    return new GeneratedParticipantDetail(username, displayName, displayName2, avatar);
  }

  public static GeneratedParticipantDetail of(final String username, final String displayName, final String avatar) {
    return new GeneratedParticipantDetail(username, displayName, null, avatar);
  }

  public static String createDisplayName(final String word1, final String word2) {
    return ToTitleCaseConverter.toTitleCase(word1) + ' ' + ToTitleCaseConverter.toTitleCase(word2);
  }

  public static String createOtherDisplayName(final String word1, final String word2, final int other) {
    return createDisplayName(word1, word2) + ' ' + other;
  }

  public static String createCacheValue(final String username, final String displayName, final String avatar) {
    return username + ":::" + displayName + ":::" + avatar;
  }

  public static GeneratedParticipantDetail getFromCachedValue(String username) {
    String displayName = username;
    String avatar = null;

    if (nonNull(username)) {
      final String[] names = username.split(":::");

      if (names.length == 3) {
        username = names[0];
        displayName = names[1];
        avatar = names[2];
      }
    }

    return GeneratedParticipantDetail.of(username, displayName, avatar);
  }
}
