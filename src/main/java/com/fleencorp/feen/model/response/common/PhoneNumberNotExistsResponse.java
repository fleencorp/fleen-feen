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
public class PhoneNumberNotExistsResponse extends EntityExistsResponse {

  @Override
  public String getMessageCode() {
    return "phone.number.not.exists";
  }

  public static PhoneNumberNotExistsResponse of(final boolean exists) {
    return PhoneNumberNotExistsResponse.builder()
      .statusCode(getActualStatusCode(exists))
      .timestamp(LocalDateTime.now())
      .exists(exists)
      .build();
  }
}
