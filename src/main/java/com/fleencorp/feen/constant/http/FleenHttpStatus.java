package com.fleencorp.feen.constant.http;

import jakarta.ws.rs.core.Response;

public final class FleenHttpStatus {

  private FleenHttpStatus() {}

  public static Response.Status ok() {
    return Response.Status.OK;
  }

  public static Response.Status created() {
    return Response.Status.CREATED;
  }

  public static Response.Status accepted() {
    return Response.Status.ACCEPTED;
  }

  public static Response.Status noContent() {
    return Response.Status.NO_CONTENT;
  }

  public static Response.Status badRequest() {
    return Response.Status.BAD_REQUEST;
  }

  public static Response.Status unauthorized() {
    return Response.Status.UNAUTHORIZED;
  }

  public static Response.Status forbidden() {
    return Response.Status.FORBIDDEN;
  }

  public static Response.Status notFound() {
    return Response.Status.NOT_FOUND;
  }

  public static Response.Status methodNotAllowed() {
    return Response.Status.METHOD_NOT_ALLOWED;
  }

  public static Response.Status conflict() {
    return Response.Status.CONFLICT;
  }

  public static Response.Status internalServerError() {
    return Response.Status.INTERNAL_SERVER_ERROR;
  }

  public static Response.Status notImplemented() {
    return Response.Status.NOT_IMPLEMENTED;
  }

  public static Response.Status badGateway() {
    return Response.Status.BAD_GATEWAY;
  }

  public static Response.Status serviceUnavailable() {
    return Response.Status.SERVICE_UNAVAILABLE;
  }

  public static Response.Status gatewayTimeout() {
    return Response.Status.GATEWAY_TIMEOUT;
  }

  public static Response.Status resetContent() {
    return Response.Status.RESET_CONTENT;
  }

  public static Response.Status partialContent() {
    return Response.Status.PARTIAL_CONTENT;
  }

  // 3xx Redirection
  public static Response.Status multipleChoices() {
    return Response.Status.MULTIPLE_CHOICES;
  }

  public static Response.Status movedPermanently() {
    return Response.Status.MOVED_PERMANENTLY;
  }

  public static Response.Status found() {
    return Response.Status.FOUND;
  }

  public static Response.Status seeOther() {
    return Response.Status.SEE_OTHER;
  }

  public static Response.Status notModified() {
    return Response.Status.NOT_MODIFIED;
  }

  public static Response.Status useProxy() {
    return Response.Status.USE_PROXY;
  }

  public static Response.Status temporaryRedirect() {
    return Response.Status.TEMPORARY_REDIRECT;
  }

  public static Response.Status permanentRedirect() {
    return Response.Status.PERMANENT_REDIRECT;
  }

  public static Response.Status paymentRequired() {
    return Response.Status.PAYMENT_REQUIRED;
  }

  public static Response.Status notAcceptable() {
    return Response.Status.NOT_ACCEPTABLE;
  }

  public static Response.Status proxyAuthenticationRequired() {
    return Response.Status.PROXY_AUTHENTICATION_REQUIRED;
  }

  public static Response.Status requestTimeout() {
    return Response.Status.REQUEST_TIMEOUT;
  }

  public static Response.Status gone() {
    return Response.Status.GONE;
  }

  public static Response.Status lengthRequired() {
    return Response.Status.LENGTH_REQUIRED;
  }

  public static Response.Status preconditionFailed() {
    return Response.Status.PRECONDITION_FAILED;
  }

  public static Response.Status unsupportedMediaType() {
    return Response.Status.UNSUPPORTED_MEDIA_TYPE;
  }

  public static Response.Status requestedRangeNotSatisfiable() {
    return Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE;
  }

  public static Response.Status expectationFailed() {
    return Response.Status.EXPECTATION_FAILED;
  }

  public static Response.Status preconditionRequired() {
    return Response.Status.PRECONDITION_REQUIRED;
  }

  public static Response.Status tooManyRequests() {
    return Response.Status.TOO_MANY_REQUESTS;
  }

  public static Response.Status requestHeaderFieldsTooLarge() {
    return Response.Status.REQUEST_HEADER_FIELDS_TOO_LARGE;
  }

  public static Response.Status unavailableForLegalReasons() {
    return Response.Status.UNAVAILABLE_FOR_LEGAL_REASONS;
  }

  public static Response.Status httpVersionNotSupported() {
    return Response.Status.HTTP_VERSION_NOT_SUPPORTED;
  }

  public static Response.Status networkAuthenticationRequired() {
    return Response.Status.NETWORK_AUTHENTICATION_REQUIRED;
  }
}
