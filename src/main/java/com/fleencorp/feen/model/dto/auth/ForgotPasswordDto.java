package com.fleencorp.feen.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToLowerCase;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.base.validator.ValidEmail;
import com.fleencorp.feen.constant.security.verification.VerificationType;
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
public class ForgotPasswordDto {

  @NotBlank(message = "{user.emailAddress.NotBlank}")
  @Size(min = 1, max = 50, message = "{user.emailAddress.Size}")
  @Email(message = "{user.emailAddress.Format}")
  @ValidEmail
  @ToLowerCase
  @JsonProperty("email_address")
  private String emailAddress;

  @NotNull(message = "{user.verificationType.NotNull}")
  @OneOf(enumClass = VerificationType.class, message = "{user.verificationType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("verification_type")
  private String verificationType;

  public VerificationType getVerificationType() {
    return VerificationType.of(verificationType);
  }

}
