package com.fleencorp.feen.model.dto.security.mfa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.constant.security.mfa.MfaType;
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
public class ConfirmMfaDto extends VerificationCodeDto {

  @NotNull(message = "{user.mfaType.NotNull}")
  @OneOf(enumClass = MfaType.class, message = "{user.mfaType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("mfa_type")
  private String mfaType;

  public MfaType getActualMfaType() {
    return MfaType.of(mfaType);
  }
}
