package com.fleencorp.feen.model.response.user.profile;

import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class UpdateProfilePhotoResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "update.profile.photo";
  }

  public static UpdateProfilePhotoResponse of() {
    return new UpdateProfilePhotoResponse();
  }
}
