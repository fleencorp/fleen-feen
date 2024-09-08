package com.fleencorp.feen.constant.http;

import org.springframework.http.HttpStatus;

public class FleenHttpStatus {

  public static HttpStatus ok() {
    return HttpStatus.OK;
  }

  public static HttpStatus created() {
    return HttpStatus.CREATED;
  }

  public static HttpStatus accepted() {
    return HttpStatus.ACCEPTED;
  }

  public static HttpStatus noContent() {
    return HttpStatus.NO_CONTENT;
  }

  public static HttpStatus badRequest() {
    return HttpStatus.BAD_REQUEST;
  }

  public static HttpStatus unauthorized() {
    return HttpStatus.UNAUTHORIZED;
  }

  public static HttpStatus forbidden() {
    return HttpStatus.FORBIDDEN;
  }

  public static HttpStatus notFound() {
    return HttpStatus.NOT_FOUND;
  }

  public static HttpStatus methodNotAllowed() {
    return HttpStatus.METHOD_NOT_ALLOWED;
  }

  public static HttpStatus conflict() {
    return HttpStatus.CONFLICT;
  }

  public static HttpStatus internalServerError() {
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }

  public static HttpStatus notImplemented() {
    return HttpStatus.NOT_IMPLEMENTED;
  }

  public static HttpStatus badGateway() {
    return HttpStatus.BAD_GATEWAY;
  }

  public static HttpStatus serviceUnavailable() {
    return HttpStatus.SERVICE_UNAVAILABLE;
  }

  public static HttpStatus gatewayTimeout() {
    return HttpStatus.GATEWAY_TIMEOUT;
  }

  public static HttpStatus continueStatus() {
    return HttpStatus.CONTINUE;
  }

  public static HttpStatus switchingProtocols() {
    return HttpStatus.SWITCHING_PROTOCOLS;
  }

  public static HttpStatus processing() {
    return HttpStatus.PROCESSING;
  }

  public static HttpStatus nonAuthoritativeInformation() {
    return HttpStatus.NON_AUTHORITATIVE_INFORMATION;
  }

  public static HttpStatus resetContent() {
    return HttpStatus.RESET_CONTENT;
  }

  public static HttpStatus partialContent() {
    return HttpStatus.PARTIAL_CONTENT;
  }

  public static HttpStatus multiStatus() {
    return HttpStatus.MULTI_STATUS;
  }

  public static HttpStatus alreadyReported() {
    return HttpStatus.ALREADY_REPORTED;
  }

  public static HttpStatus imUsed() {
    return HttpStatus.IM_USED;
  }

  // 3xx Redirection
  public static HttpStatus multipleChoices() {
    return HttpStatus.MULTIPLE_CHOICES;
  }

  public static HttpStatus movedPermanently() {
    return HttpStatus.MOVED_PERMANENTLY;
  }

  public static HttpStatus found() {
    return HttpStatus.FOUND;
  }

  public static HttpStatus seeOther() {
    return HttpStatus.SEE_OTHER;
  }

  public static HttpStatus notModified() {
    return HttpStatus.NOT_MODIFIED;
  }

  public static HttpStatus useProxy() {
    return HttpStatus.USE_PROXY;
  }

  public static HttpStatus temporaryRedirect() {
    return HttpStatus.TEMPORARY_REDIRECT;
  }

  public static HttpStatus permanentRedirect() {
    return HttpStatus.PERMANENT_REDIRECT;
  }

  public static HttpStatus paymentRequired() {
    return HttpStatus.PAYMENT_REQUIRED;
  }

  public static HttpStatus notAcceptable() {
    return HttpStatus.NOT_ACCEPTABLE;
  }

  public static HttpStatus proxyAuthenticationRequired() {
    return HttpStatus.PROXY_AUTHENTICATION_REQUIRED;
  }

  public static HttpStatus requestTimeout() {
    return HttpStatus.REQUEST_TIMEOUT;
  }

  public static HttpStatus gone() {
    return HttpStatus.GONE;
  }

  public static HttpStatus lengthRequired() {
    return HttpStatus.LENGTH_REQUIRED;
  }

  public static HttpStatus preconditionFailed() {
    return HttpStatus.PRECONDITION_FAILED;
  }

  public static HttpStatus payloadTooLarge() {
    return HttpStatus.PAYLOAD_TOO_LARGE;
  }

  public static HttpStatus uriTooLong() {
    return HttpStatus.URI_TOO_LONG;
  }

  public static HttpStatus unsupportedMediaType() {
    return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
  }

  public static HttpStatus requestedRangeNotSatisfiable() {
    return HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
  }

  public static HttpStatus expectationFailed() {
    return HttpStatus.EXPECTATION_FAILED;
  }

  public static HttpStatus iAmATeapot() {
    return HttpStatus.I_AM_A_TEAPOT;
  }

  public static HttpStatus unprocessableEntity() {
    return HttpStatus.UNPROCESSABLE_ENTITY;
  }

  public static HttpStatus locked() {
    return HttpStatus.LOCKED;
  }

  public static HttpStatus failedDependency() {
    return HttpStatus.FAILED_DEPENDENCY;
  }

  public static HttpStatus upgradeRequired() {
    return HttpStatus.UPGRADE_REQUIRED;
  }

  public static HttpStatus preconditionRequired() {
    return HttpStatus.PRECONDITION_REQUIRED;
  }

  public static HttpStatus tooManyRequests() {
    return HttpStatus.TOO_MANY_REQUESTS;
  }

  public static HttpStatus requestHeaderFieldsTooLarge() {
    return HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE;
  }

  public static HttpStatus unavailableForLegalReasons() {
    return HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS;
  }

  public static HttpStatus httpVersionNotSupported() {
    return HttpStatus.HTTP_VERSION_NOT_SUPPORTED;
  }

  public static HttpStatus variantAlsoNegotiates() {
    return HttpStatus.VARIANT_ALSO_NEGOTIATES;
  }

  public static HttpStatus insufficientStorage() {
    return HttpStatus.INSUFFICIENT_STORAGE;
  }

  public static HttpStatus loopDetected() {
    return HttpStatus.LOOP_DETECTED;
  }

  public static HttpStatus notExtended() {
    return HttpStatus.NOT_EXTENDED;
  }

  public static HttpStatus networkAuthenticationRequired() {
    return HttpStatus.NETWORK_AUTHENTICATION_REQUIRED;
  }
}
