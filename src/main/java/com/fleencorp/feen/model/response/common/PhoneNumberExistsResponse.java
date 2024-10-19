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
public class PhoneNumberExistsResponse extends EntityExistsResponse {

  @Override
  public String getMessageCode() {
    return "phone.number.exists";
  }

  public static PhoneNumberExistsResponse of(final boolean exists) {
    return PhoneNumberExistsResponse.builder()
      .statusCode(getActualStatusCode(exists))
      .timestamp(LocalDateTime.now())
      .exists(exists)
      .build();
  }

}
