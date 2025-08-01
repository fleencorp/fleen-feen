package com.fleencorp.feen.common.constant.http;

public final class StatusCodeMessage {

  public static final String RESPONSE_400 = "Invalid input parameters provided or request body";
  public static final String RESPONSE_500 = "Internal server error";
  public static final String RESPONSE_404 = "Not found";
  public static final String RESPONSE_409 = "Conflict";
  public static final String RESPONSE_202 = "Accepted";

  private StatusCodeMessage() {}
}
