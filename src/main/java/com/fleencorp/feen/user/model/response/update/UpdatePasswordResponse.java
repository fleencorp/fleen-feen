package com.fleencorp.feen.user.model.response.update;

import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePasswordResponse extends LocalizedResponse {

  @Override
  public String getMessageCode() {
    return "update.password";
  }

  public static UpdatePasswordResponse of() {
    return new UpdatePasswordResponse();
  }
}
