package com.fleencorp.feen.model.request.calendar.calendar;

import com.fleencorp.feen.constant.external.google.calendar.calendar.AclRole;
import com.fleencorp.feen.constant.external.google.calendar.calendar.AclScopeType;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShareCalendarWithUserRequest {

  private String calendarId;
  private String emailAddress;
  private AclScopeType aclScopeType;
  private AclRole aclRole;
}
