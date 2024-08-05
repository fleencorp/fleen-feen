package com.fleencorp.feen.model.dto.security.mfa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.converter.common.ToUpperCase;
import com.fleencorp.feen.model.dto.security.VerificationCodeDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmMfaVerificationCodeDto extends VerificationCodeDto {

  @NotNull(message = "{user.mfaType.NotNull}")
  @ValidEnum(enumClass = MfaType.class, message = "{user.mfaType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("mfa_type")
  private String mfaType;

  public MfaType getActualMfaType() {
    return parseEnumOrNull(mfaType, MfaType.class);
  }
}
