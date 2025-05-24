package com.fleencorp.feen.model.response.user.profile.update;

import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProfileInfoResponse extends LocalizedResponse {

  @Override
  public String getMessageCode() {
    return "update.profile.info";
  }

  public static UpdateProfileInfoResponse of() {
    return new UpdateProfileInfoResponse();
  }
}
