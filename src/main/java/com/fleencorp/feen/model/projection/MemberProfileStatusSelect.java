package com.fleencorp.feen.model.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonProperty("profile_status")
  private ProfileStatus profileStatus;
}
