package com.fleencorp.feen.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.security.profile.ProfileVerificationType;
import com.fleencorp.feen.converter.ToUpperCase;
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
public class CompleteSignUpDto extends VerificationCodeDto {

  @NotNull(message = "{user.verificationType.NotNull}")
  @ValidEnum(enumClass = ProfileVerificationType.class, message = "{user.verificationType.Type}")
  @ToUpperCase
  @JsonProperty("profile_verification_type")
  private String profileVerificationType;

  public ProfileVerificationType getActualProfileVerificationType() {
    return parseEnumOrNull(profileVerificationType, ProfileVerificationType.class);
  }
}
