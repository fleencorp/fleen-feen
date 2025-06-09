package com.fleencorp.feen.user.model.projection;

import com.fleencorp.feen.user.constant.profile.ProfileStatus;
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
