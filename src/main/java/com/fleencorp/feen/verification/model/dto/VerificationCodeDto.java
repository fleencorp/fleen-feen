package com.fleencorp.feen.verification.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class VerificationCodeDto {

  @NotBlank(message = "{verificationCode.NotBlank}")
  @Size(min = 1, max = 6, message = "{verificationCode.Size}")
  @JsonProperty("verification_code")
  private String verificationCode;
}
