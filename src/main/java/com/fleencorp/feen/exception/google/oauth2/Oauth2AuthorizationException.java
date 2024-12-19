package com.fleencorp.feen.exception.google.oauth2;

import static java.util.Objects.nonNull;

public final class Oauth2AuthorizationException {

  private Oauth2AuthorizationException() {}

  public static String failedVerificationOfAuthorizationCodeMessage(final String message) {
    return String.format("An error occurred while exchanging Oauth2 authorization code. Reason: %s", message);
  }

  public static String failedTokenRefresh(final String message) {
    return String.format("An error occurred while refreshing Google Oauth2 token. Reason: %s", message);
  }

  public static boolean isInvalidGrant(final String message) {
    return nonNull(message) && message.contains("invalid_grant");
  }
}
