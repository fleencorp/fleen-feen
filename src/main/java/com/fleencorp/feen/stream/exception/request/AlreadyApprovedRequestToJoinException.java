package com.fleencorp.feen.stream.exception.request;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class AlreadyApprovedRequestToJoinException extends LocalizedException {

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
