package com.fleencorp.feen.model.info.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "status",
  "profile_status_text"
})
public class ProfileStatusInfo {

  @JsonProperty("status")
  private ProfileStatus status;

  @JsonProperty("profile_status_text")
  private String profileStatusText;

  public static ProfileStatusInfo of(final ProfileStatus status, final String profileStatusText) {
    return new ProfileStatusInfo(status, profileStatusText);
  }
}


