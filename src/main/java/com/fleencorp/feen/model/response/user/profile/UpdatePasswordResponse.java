package com.fleencorp.feen.model.response.user.profile;

import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePasswordResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "update.password";
  }

  public static UpdatePasswordResponse of() {
    return new UpdatePasswordResponse();
  }
}
