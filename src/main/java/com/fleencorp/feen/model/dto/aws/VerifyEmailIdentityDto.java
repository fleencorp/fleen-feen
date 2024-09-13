package com.fleencorp.feen.model.dto.aws;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailIdentityDto {

  @NotNull
  @JsonProperty("email_addresses")
  private List<String> emailAddresses;

  public boolean isEmailsValid() {
    return nonNull(emailAddresses) && !emailAddresses.isEmpty();
  }

  public static boolean isEmailValid(final String emailAddress) {
    return nonNull(emailAddress) && !(emailAddress.trim().isBlank());
  }
}
