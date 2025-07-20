package com.fleencorp.feen.common.model.response;

import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PhoneNumberExistsResponse extends FleenFeenResponse.EntityExistsResponse {

  public PhoneNumberExistsResponse(final boolean exists) {
    super(exists, LocalDateTime.now(), getActualStatusCode(exists));
  }

  @Override
  public String getMessageCode() {
    return exists ? "phone.number.exists" : "phone.number.not.exists";
  }

  public static PhoneNumberExistsResponse of(final boolean exists) {
    return new PhoneNumberExistsResponse(exists);
  }

}
