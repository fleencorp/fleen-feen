package com.fleencorp.feen.model.projection.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoSelect {

  protected Long memberId;
  protected String firstName;
  protected String lastName;
  protected String profilePhoto;
  protected String country;
}
