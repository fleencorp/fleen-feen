package com.fleencorp.feen.model.response.common;

import com.fleencorp.feen.model.response.other.EntityExistsResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EmailAddressNotExistsResponse extends EntityExistsResponse {

  public EmailAddressNotExistsResponse(final boolean exists) {
    super(exists, LocalDateTime.now(), getActualStatusCode(exists));
  }

  @Override
  public String getMessageCode() {
    return "email.address.not.exists";
  }

  public static EmailAddressNotExistsResponse of(final boolean exists) {
    return new EmailAddressNotExistsResponse(exists);
  }
}
