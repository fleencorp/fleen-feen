package com.fleencorp.feen.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.*;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.converter.common.ToLowerCase;
import com.fleencorp.feen.converter.common.ToTitleCase;
import com.fleencorp.feen.converter.common.ToUpperCase;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.validator.CountryExist;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldMatch.List({
  @FieldMatch(first = "password", second = "confirmPassword", message = "{passwordConfirmation.Equal}")
})
public class SignUpDto {

  @NotBlank(message = "{user.firstName.NotBlank}")
  @Size(min = 2, max = 50, message = "{user.firstName.Size}")
  @ToTitleCase
  @JsonProperty("first_name")
  private String firstName;

  @NotBlank(message = "{user.lastName.NotBlank}")
  @Size(min = 2, max = 50, message = "{user.lastName.Size}")
  @ToTitleCase
  @JsonProperty("last_name")
  private String lastName;

  @NotBlank(message = "{user.emailAddress.NotBlank}")
  @Size(min = 4, max = 150, message = "{user.emailAddress.Size}")
  @Email(message = "{user.emailAddress.Format}")
  @ValidEmail
  @EmailAddressAlreadyExist
  @ToLowerCase
  @JsonProperty("email_address")
  private String emailAddress;

  @NotBlank(message = "{user.phoneNumber.NotBlank}")
  @Size(min = 4, max = 20, message = "{user.phoneNumber.Size}")
  @ValidPhoneNumber
  @PhoneNumberAlreadyExist
  @JsonProperty("phone_number")
  private String phoneNumber;

  @NotBlank(message = "{user.password.NotBlank}")
  @Size(min = 8, max = 24, message = "{user.password.Size}")
  @ValidPassword
  private String password;

  @NotBlank(message = "{user.confirmPassword.NotBlank}")
  @Size(min = 8, max = 24, message = "{user.confirmPassword.Size}")
  @ValidPassword
  @JsonProperty("confirm_password")
  private String confirmPassword;

  @NotNull(message = "{user.country.NotNull}")
  @Size(max = 1000, message = "{user.country.Size}")
  @CountryExist
  @JsonProperty("country_code")
  private String countryCode;

  @NotNull(message = "{user.verificationType.NotNull}")
  @ValidEnum(enumClass = VerificationType.class, message = "{user.verificationType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("verification_type")
  private String verificationType;

  public VerificationType getActualVerificationType() {
    return VerificationType.of(verificationType);
  }

  public Member toMember() {
    return Member.builder()
        .firstName(firstName)
        .lastName(lastName)
        .emailAddress(emailAddress)
        .phoneNumber(phoneNumber)
        .password(password)
        .build();
  }
}
