package com.fleencorp.feen.model.response.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class EmailAddressNotExistsResponse extends EmailAddressExistsResponse {

  @Override
  public String getMessageCode() {
    return "email.address.not.exists";
  }

  public static EmailAddressNotExistsResponse of(boolean exists) {
    return EmailAddressNotExistsResponse.builder()
      .statusCode(getActualStatusCode(true))
      .timestamp(LocalDateTime.now())
      .exists(exists)
      .build();
  }
}
