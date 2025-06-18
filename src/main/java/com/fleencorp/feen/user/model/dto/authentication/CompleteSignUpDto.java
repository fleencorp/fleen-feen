package com.fleencorp.feen.user.model.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.verification.constant.VerificationType;
import com.fleencorp.feen.verification.model.dto.VerificationCodeDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompleteSignUpDto extends VerificationCodeDto {

  @NotNull(message = "{user.verificationType.NotNull}")
  @OneOf(enumClass = VerificationType.class, message = "{user.verificationType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("verification_type")
  private String verificationType;

  public VerificationType getVerificationType() {
    return VerificationType.of(verificationType);
  }
}
