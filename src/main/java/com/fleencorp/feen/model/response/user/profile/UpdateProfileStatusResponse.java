package com.fleencorp.feen.model.response.user.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "message",
  "status"
})
public class UpdateProfileStatusResponse extends ApiResponse {

  @JsonProperty("status")
  private ProfileStatus status;

  @Override
  public String getMessageCode() {
    return "update.profile.status";
  }

  public static UpdateProfileStatusResponse of(final ProfileStatus profileStatus) {
    return UpdateProfileStatusResponse.builder()
      .status(profileStatus)
      .build();
  }
}
