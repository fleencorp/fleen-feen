package com.fleencorp.feen.model.response.common;

import com.fleencorp.feen.model.response.other.EntityExistsResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PhoneNumberNotExistsResponse extends EntityExistsResponse {

  public PhoneNumberNotExistsResponse(final boolean exists) {
    super(exists, LocalDateTime.now(), getActualStatusCode(exists));
  }

  @Override
  public String getMessageCode() {
    return "phone.number.not.exists";
  }

  public static PhoneNumberNotExistsResponse of(final boolean exists) {
    return new PhoneNumberNotExistsResponse(exists);
  }
}
