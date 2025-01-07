package com.fleencorp.feen.model.response.common;

import com.fleencorp.feen.model.response.other.EntityExistsResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PhoneNumberExistsResponse extends EntityExistsResponse {

  public PhoneNumberExistsResponse(final boolean exists) {
    super(exists, LocalDateTime.now(), getActualStatusCode(exists));
  }

  @Override
  public String getMessageCode() {
    return "phone.number.exists";
  }

  public static PhoneNumberExistsResponse of(final boolean exists) {
    return new PhoneNumberExistsResponse(exists);
  }

}
