package com.fleencorp.feen.user.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.ValidPhoneNumber;
import com.fleencorp.feen.model.dto.security.VerificationCodeDto;
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
public class ConfirmUpdatePhoneNumberDto extends VerificationCodeDto {

  @NotBlank(message = "{user.phoneNumber.NotBlank}")
  @Size(min = 4, max = 20, message = "{user.phoneNumber.Size}")
  @ValidPhoneNumber
  @JsonProperty("phone_number")
  private String phoneNumber;
}
