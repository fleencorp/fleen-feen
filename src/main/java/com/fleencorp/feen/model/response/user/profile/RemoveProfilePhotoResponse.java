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
public class RemoveProfilePhotoResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "remove.profile.photo";
  }

  public static RemoveProfilePhotoResponse of() {
    return new RemoveProfilePhotoResponse();
  }
}
