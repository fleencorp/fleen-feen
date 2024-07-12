package com.fleencorp.feen.model.dto.verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.ValidEmail;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.base.validator.ValidPhoneNumber;
import com.fleencorp.feen.constant.security.profile.ProfileVerificationType;
import com.fleencorp.feen.converter.ToLowerCase;
import com.fleencorp.feen.converter.ToUpperCase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ResendVerificationCodeDto {

  @Size(min = 1, max = 50, message = "{user.emailAddress.Size}")
  @Email(message = "{user.emailAddress.Format}")
  @ValidEmail
  @ToLowerCase
  @JsonProperty("email_address")
  private String emailAddress;

  @Size(min = 4, max = 15, message = "{user.phoneNumber.Size}")
  @ValidPhoneNumber
  @JsonProperty("phone_number")
  private String phoneNumber;

  @NotNull(message = "{user.verificationType.NotNull}")
  @ValidEnum(enumClass = ProfileVerificationType.class, message = "{user.verificationType.Type}")
  @ToUpperCase
  @JsonProperty("profile_verification_type")
  private String profileVerificationType;

  public ProfileVerificationType getActualProfileVerificationType() {
    return parseEnumOrNull(profileVerificationType, ProfileVerificationType.class);
  }
}
