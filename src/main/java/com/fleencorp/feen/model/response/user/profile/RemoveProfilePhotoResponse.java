package com.fleencorp.feen.model.response.user.profile;

import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoveProfilePhotoResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "remove.profile.photo";
  }

  public static RemoveProfilePhotoResponse of() {
    return new RemoveProfilePhotoResponse();
  }
}
