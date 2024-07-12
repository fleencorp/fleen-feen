package com.fleencorp.feen.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.ValidEmail;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.converter.ToLowerCase;
import com.fleencorp.feen.converter.ToUpperCase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

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
  @ValidEnum(enumClass = VerificationType.class, message = "{user.verificationType.Type}")
  @ToUpperCase
  @JsonProperty("verification_type")
  private String verificationType;

  public VerificationType getActualVerificationType() {
    return parseEnumOrNull(getVerificationType(), VerificationType.class);
  }

}
