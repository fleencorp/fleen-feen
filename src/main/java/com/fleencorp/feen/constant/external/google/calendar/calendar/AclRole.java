package com.fleencorp.feen.constant.external.google.calendar.calendar;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
* Enum representing roles in Access Control List (ACL).
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum AclRole implements ApiParameter {


  /**
   * Reader role allows the user to view all event details in the calendar.
   * Users with this role can see event information, but they cannot make any changes.
   */
  READER("reader"),

  /**
   * Writer role allows the user to view, create, and modify events in the calendar.
   * Users with this role can add new events, edit existing events, and manage event details.
   */
  WRITER("writer"),

  /**
   * Free Busy Reader role allows the user to see free/busy information for the calendar without
   * exposing any specific event details. This is useful for determining availability without
   * revealing sensitive event information.
   */
  FREE_BUSY_READER("freeBusyReader"),

  /**
   * Owner role provides full access to the calendar, including managing settings and sharing permissions.
   * Users with this role can perform any action, such as editing, deleting events, and changing access permissions.
   */
  OWNER("owner");

  private final String value;

  AclRole(final String value) {
    this.value = value;
  }

  public static AclRole of(final String value) {
    return parseEnumOrNull(value, AclRole.class);
  }

}
