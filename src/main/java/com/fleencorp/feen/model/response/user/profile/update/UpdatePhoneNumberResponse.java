package com.fleencorp.feen.model.response.user.profile.update;

import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePhoneNumberResponse extends LocalizedResponse {

  @Override
  public String getMessageCode() {
    return "update.phone.number";
  }

  public static UpdatePhoneNumberResponse of() {
    return new UpdatePhoneNumberResponse();
  }
}
