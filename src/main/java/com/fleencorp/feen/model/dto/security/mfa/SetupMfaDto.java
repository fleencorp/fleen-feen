package com.fleencorp.feen.model.dto.security.mfa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.converter.common.ToUpperCase;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetupMfaDto {

  @NotNull(message = "{user.mfaType.NotNull}")
  @ValidEnum(enumClass = MfaType.class, message = "{user.mfaType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("mfa_type")
  private String mfaType;

  public MfaType getActualMfaType() {
    return parseEnumOrNull(getMfaType(), MfaType.class);
  }
}
