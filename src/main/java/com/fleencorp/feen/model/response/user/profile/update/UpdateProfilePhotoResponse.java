package com.fleencorp.feen.model.response.user.profile.update;

import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
