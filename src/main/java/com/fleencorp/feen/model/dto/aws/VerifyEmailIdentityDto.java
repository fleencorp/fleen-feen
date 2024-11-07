package com.fleencorp.feen.model.dto.aws;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.util.StringUtil;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailIdentityDto {

  @NotNull(message = "{emailAddress.NotEmpty}")
  @JsonProperty("email_addresses")
  private List<String> emailAddresses;

  public boolean isEmailsValid() {
    return nonNull(emailAddresses) && !emailAddresses.isEmpty();
  }

  /**
   * Checks if the given email address is valid.
   *
   * <p>A valid email address is considered to be non-null, non-empty, and not
   * entirely composed of whitespace characters.</p>
   *
   * @param emailAddress The email address to validate.
   * @return True if the email address is valid, false otherwise.
   */
  public static boolean isEmailValid(final String emailAddress) {
    return nonNull(emailAddress) && !(emailAddress.trim().isBlank())
      && StringUtil.isValidEmail(emailAddress);
  }

  /**
   * Retrieves a list of valid email addresses.
   *
   * <p>This method attempts to retrieve a list of email addresses and filters out any
   * invalid addresses before returning the list.</p>
   *
   * @return A list of valid email addresses as Strings. If no valid email addresses
   * are found, an empty list is returned.
   */
  public List<String> getEmailAddresses() {
    if (isEmailsValid()) {
      return emailAddresses.stream()
        .filter(Objects::nonNull)
        .filter(VerifyEmailIdentityDto::isEmailValid)
        .toList();
    }
    return new ArrayList<>();
  }
}
