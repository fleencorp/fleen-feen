package com.fleencorp.feen.model.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import lombok.*;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenPayload {

  private Long userId;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String sub;
  private String status;
  private String[] authorities;
  private String profilePhoto;

  public ProfileStatus getProfileStatus() {
    return parseEnumOrNull(status, ProfileStatus.class);
  }
}
