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
public class UpdateEmailAddressResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "update.email.address";
  }

  public static UpdateEmailAddressResponse of() {
    return new UpdateEmailAddressResponse();
  }
}
