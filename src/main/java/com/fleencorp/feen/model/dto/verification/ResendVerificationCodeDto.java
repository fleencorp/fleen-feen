package com.fleencorp.feen.model.dto.verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToLowerCase;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.ValidEmail;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.base.validator.ValidPhoneNumber;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
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
  @ValidEnum(enumClass = VerificationType.class, message = "{user.verificationType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("verification_type")
  private String verificationType;

  public VerificationType getActualVerificationType() {
    return VerificationType.of(verificationType);
  }
}
