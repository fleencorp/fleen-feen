package com.fleencorp.feen.constant.share;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing the status of a contact share request.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum ShareContactRequestStatus implements ApiParameter {

  CANCELED("Canceled"),
  ACCEPTED("Confirmed"),
  REJECTED("Rejected"),
  SENT("Sent");

  private final String value;

  ShareContactRequestStatus(final String value) {
    this.value = value;
  }
}
