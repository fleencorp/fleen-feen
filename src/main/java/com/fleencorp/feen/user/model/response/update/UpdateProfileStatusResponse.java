package com.fleencorp.feen.user.model.response.update;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.user.model.info.ProfileStatusInfo;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "message",
  "profile_status_info"
})
public class UpdateProfileStatusResponse extends LocalizedResponse {

  @JsonProperty("profile_status_info")
  private ProfileStatusInfo profileStatusInfo;

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "update.profile.status";
  }

  public static UpdateProfileStatusResponse of(final ProfileStatusInfo profileStatusInfo) {
    return new UpdateProfileStatusResponse(profileStatusInfo);
  }
}
