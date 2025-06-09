package com.fleencorp.feen.user.model.dto.security.mfa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.user.constant.mfa.MfaType;
import com.fleencorp.feen.user.model.dto.security.VerificationCodeDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  public MfaType getMfaType() {
    return MfaType.of(mfaType);
  }
}
