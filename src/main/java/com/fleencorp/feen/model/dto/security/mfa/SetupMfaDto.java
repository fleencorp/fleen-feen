package com.fleencorp.feen.model.dto.security.mfa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetupMfaDto {

  @NotNull(message = "{user.mfaType.NotNull}")
  @OneOf(enumClass = MfaType.class, message = "{user.mfaType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("mfa_type")
  private String mfaType;

  public MfaType getActualMfaType() {
    return MfaType.of(mfaType);
  }
}
