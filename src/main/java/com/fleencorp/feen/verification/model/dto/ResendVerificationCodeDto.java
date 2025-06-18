package com.fleencorp.feen.verification.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToLowerCase;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.base.validator.ValidEmail;
import com.fleencorp.base.validator.ValidPhoneNumber;
import com.fleencorp.feen.verification.constant.VerificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResendVerificationCodeDto {

  @Size(min = 1, max = 50, message = "{user.emailAddress.Size}")
  @Email(message = "{user.emailAddress.Format}")
  @ValidEmail
  @ToLowerCase
  @JsonProperty("email_address")
  private String emailAddress;

  @Size(min = 4, max = 20, message = "{user.phoneNumber.Size}")
  @ValidPhoneNumber
  @JsonProperty("phone_number")
  private String phoneNumber;

  @NotNull(message = "{user.verificationType.NotNull}")
  @OneOf(enumClass = VerificationType.class, message = "{user.verificationType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("verification_type")
  private String verificationType;

  public VerificationType getVerificationType() {
    return VerificationType.of(verificationType);
  }
}
