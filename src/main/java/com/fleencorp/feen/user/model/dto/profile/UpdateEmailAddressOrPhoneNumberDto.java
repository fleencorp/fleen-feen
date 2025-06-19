package com.fleencorp.feen.user.model.dto.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.verification.constant.VerificationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailAddressOrPhoneNumberDto {

  @NotNull(message = "{user.verificationType.NotNull}")
  @OneOf(enumClass = VerificationType.class, message = "{user.verificationType.Type}")
  @JsonProperty("verification_type")
  private String verificationType;

  public VerificationType getVerificationType() {
    return VerificationType.valueOf(verificationType);
  }
}
