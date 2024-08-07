package com.fleencorp.feen.constant.external.google.calendar.calendar;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enum representing roles in Access Control List (ACL).
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum AclRole implements ApiParameter {

  READER("reader"),
  WRITER("writer"),
  FREE_BUSY_READER("freeBusyReader"),
  OWNER("owner");

  private final String value;

  AclRole(final String value) {
    this.value = value;
  }

}
