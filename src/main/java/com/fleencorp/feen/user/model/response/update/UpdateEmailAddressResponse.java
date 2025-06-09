package com.fleencorp.feen.user.model.response.update;

import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateEmailAddressResponse extends LocalizedResponse {

  @Override
  public String getMessageCode() {
    return "update.email.address";
  }

  public static UpdateEmailAddressResponse of() {
    return new UpdateEmailAddressResponse();
  }
}
