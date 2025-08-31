package com.fleencorp.feen.calendar.model.request.calendar;

import com.fleencorp.feen.common.constant.external.google.calendar.calendar.AclRole;
import com.fleencorp.feen.common.constant.external.google.calendar.calendar.AclScopeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShareCalendarWithUserRequest extends CalendarRequest {

  private String calendarId;
  private String emailAddress;
  private AclScopeType aclScopeType;
  private AclRole aclRole;

  public static ShareCalendarWithUserRequest of(
      final String calendarId,
      final String emailAddress,
      final AclScopeType aclScopeType,
      final AclRole aclRole,
      final String accessToken) {
    final ShareCalendarWithUserRequest request = new ShareCalendarWithUserRequest();
    request.setCalendarId(calendarId);
    request.setEmailAddress(emailAddress);
    request.setAclScopeType(aclScopeType);
    request.setAclRole(aclRole);
    request.setAccessToken(accessToken);

    return request;
  }

  public static ShareCalendarWithUserRequest of(
      final String calendarId,
      final String emailAddress,
      final String accessToken) {
    final ShareCalendarWithUserRequest request = new ShareCalendarWithUserRequest();
    request.setCalendarId(calendarId);
    request.setEmailAddress(emailAddress);
    request.setAccessToken(accessToken);
    request.setAclRole(AclRole.READER);
    request.setAclScopeType(AclScopeType.USER);

    return request;
  }
}
