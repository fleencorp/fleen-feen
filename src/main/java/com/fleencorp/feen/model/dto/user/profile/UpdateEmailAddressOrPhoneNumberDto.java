package com.fleencorp.feen.model.dto.user.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailAddressOrPhoneNumberDto {

  @NotNull(message = "{user.verificationType.NotNull}")
  @ValidEnum(enumClass = VerificationType.class, message = "{user.verificationType.Type}")
  @JsonProperty("verification_type")
  private String verificationType;

  public VerificationType getActualVerificationType() {
    return VerificationType.valueOf(verificationType);
  }
}
