package com.fleencorp.feen.model.response.common;

import com.fleencorp.feen.model.response.other.EntityExistsResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class EmailAddressExistsResponse extends EntityExistsResponse {

  @Override
  public String getMessageCode() {
    return "email.address.exists";
  }

  public static EmailAddressExistsResponse of(boolean exists) {
    return EmailAddressExistsResponse.builder()
      .statusCode(getActualStatusCode(true))
      .timestamp(LocalDateTime.now())
      .exists(exists)
      .build();
  }
}
