package com.fleencorp.feen.constant.user;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum IsEligibleForContactRequest implements ApiParameter {

  NO("No", "is.eligible.for.contact.request.no", "is.eligible.for.contact.request.no.2", "is.eligible.for.contact.request.no.3"),
  YES("Yes", "is.eligible.for.contact.request.yes", "is.eligible.for.contact.request.yes.2", "is.eligible.for.contact.request.yes.3");

  private final String value;
  private final String messageCode;
  private final String messageCode2;
  private final String messageCode3;

  IsEligibleForContactRequest(
      final String value,
      final String messageCode,
      final String messageCode2,
      final String messageCode3) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
    this.messageCode3 = messageCode3;
  }

  public static IsEligibleForContactRequest by(final boolean isEligibleForContactRequest) {
    return isEligibleForContactRequest ? YES : NO;
  }
}

