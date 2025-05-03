package com.fleencorp.feen.model.projection.member;

import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileStatusSelect {

  private ProfileStatus profileStatus;
}
