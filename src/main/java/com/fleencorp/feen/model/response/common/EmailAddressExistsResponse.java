package com.fleencorp.feen.model.response.common;

import com.fleencorp.feen.model.response.other.EntityExistsResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EmailAddressExistsResponse extends EntityExistsResponse {

  public EmailAddressExistsResponse(final boolean exists) {
    super(exists, LocalDateTime.now(), getActualStatusCode(exists));
  }

  @Override
  public String getMessageCode() {
    return exists ? "email.address.exists" : "email.address.not.exists";
  }

  public static EmailAddressExistsResponse of(final boolean exists) {
    return new EmailAddressExistsResponse(exists);
  }
}
