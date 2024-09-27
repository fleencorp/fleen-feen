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
public class UpdatePasswordResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "update.password";
  }

  public static UpdatePasswordResponse of() {
    return new UpdatePasswordResponse();
  }
}
