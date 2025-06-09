package com.fleencorp.feen.model.response.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.feen.user.constant.profile.ProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileStatusResponse {

  @JsonProperty("profile_status")
  private ProfileStatus profileStatus;

  public static MemberProfileStatusResponse of(final ProfileStatus profileStatus) {
    return new MemberProfileStatusResponse(profileStatus);
  }
}
