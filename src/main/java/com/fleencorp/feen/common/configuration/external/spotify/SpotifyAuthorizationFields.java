package com.fleencorp.feen.common.configuration.external.spotify;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.nonNull;

public class SpotifyAuthorizationFields {

  public static final String CLIENT_ID = "client_id";
  public static final String CODE = "code";
  public static final String SCOPE = "scope";
  public static final String REDIRECT_URI = "redirect_uri";
  public static final String RESPONSE_TYPE = "response_type";
  public static final String STATE = "state";

  public static String clientId() {
    return CLIENT_ID;
  }

  public static String code() {
    return CODE;
  }

  public static String scope() {
    return SCOPE;
  }

  public static String redirectUri() {
    return REDIRECT_URI;
  }

  public static String responseType() {
    return RESPONSE_TYPE;
  }

  public static String state() {
    return STATE;
  }

  public static String encode(final String value) {
    return nonNull(value) ? URLEncoder.encode(value, StandardCharsets.UTF_8) : null;
  }
}
