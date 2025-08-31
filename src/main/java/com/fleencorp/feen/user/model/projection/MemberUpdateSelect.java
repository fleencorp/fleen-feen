package com.fleencorp.feen.user.model.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateSelect {

  protected Long memberId;
  protected String firstName;
  protected String lastName;
  protected String emailAddress;
  protected String phoneNumber;
  protected String country;
}
