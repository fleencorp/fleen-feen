package com.fleencorp.feen.exception.stream.join.request;

import com.fleencorp.localizer.model.exception.ApiException;

public class AlreadyApprovedRequestToJoinException extends ApiException {

  @Override
  public String getMessageCode() {
    return "already.approved.requested.to.join";
  }

  public AlreadyApprovedRequestToJoinException(final Object...params) {
    super(params);
  }

  public static AlreadyApprovedRequestToJoinException of(final Object status) {
    return new AlreadyApprovedRequestToJoinException(status);
  }
}
