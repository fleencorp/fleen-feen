package com.fleencorp.feen.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.model.dto.security.VerificationCodeDto;
import jakarta.validation.constraints.NotNull;
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
public class CompleteSignUpDto extends VerificationCodeDto {

  @NotNull(message = "{user.verificationType.NotNull}")
  @ValidEnum(enumClass = VerificationType.class, message = "{user.verificationType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("verification_type")
  private String verificationType;

  public VerificationType getActualVerificationType() {
    return VerificationType.of(verificationType);
  }
}
