package com.fleencorp.feen.calendar.model.request.calendar;

import com.fleencorp.feen.common.constant.external.google.calendar.calendar.AclRole;
import com.fleencorp.feen.common.constant.external.google.calendar.calendar.AclScopeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShareCalendarWithUserRequest extends CalendarRequest {

  private String calendarId;
  private String emailAddress;
  private AclScopeType aclScopeType;
  private AclRole aclRole;

  public static ShareCalendarWithUserRequest of(final String calendarId, final String emailAddress, final AclScopeType aclScopeType, final AclRole aclRole, final String accessToken) {
    return ShareCalendarWithUserRequest.builder()
      .calendarId(calendarId)
      .emailAddress(emailAddress)
      .aclScopeType(aclScopeType)
      .aclRole(aclRole)
      .accessToken(accessToken)
      .build();
  }

  public static ShareCalendarWithUserRequest of(final String calendarId, final String emailAddress, final String accessToken) {
    return ShareCalendarWithUserRequest.builder()
      .calendarId(calendarId)
      .emailAddress(emailAddress)
      .accessToken(accessToken)
      .aclScopeType(AclScopeType.USER)
      .aclRole(AclRole.READER)
      .build();
  }
}
