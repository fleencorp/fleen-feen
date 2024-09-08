package com.fleencorp.feen.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToLowerCase;
import com.fleencorp.base.validator.ValidEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignInDto {

  @NotBlank(message = "{user.emailAddress.NotBlank}")
  @Size(min = 1, max = 50, message = "{user.emailAddress.Size}")
  @Email(message = "{user.emailAddress.Format}")
  @ValidEmail
  @ToLowerCase
  @JsonProperty("email_address")
  private String emailAddress;

  @NotBlank(message = "{user.password.NotBlank}")
  @Size(min = 8, max = 24, message = "{user.password.Size}")
  private String password;
}
