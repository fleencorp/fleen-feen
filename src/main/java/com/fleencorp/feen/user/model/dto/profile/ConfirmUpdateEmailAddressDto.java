package com.fleencorp.feen.user.model.dto.profile;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToLowerCase;
import com.fleencorp.base.validator.ValidEmail;
import com.fleencorp.feen.user.model.dto.security.VerificationCodeDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmUpdateEmailAddressDto extends VerificationCodeDto {

  @NotBlank(message = "{user.emailAddress.NotBlank}")
  @Size(min = 4, max = 150, message = "{user.emailAddress.Size}")
  @Email(message = "{user.emailAddress.Format}")
  @ValidEmail
  @ToLowerCase
  @JsonProperty("email_address")
  private String emailAddress;
}
