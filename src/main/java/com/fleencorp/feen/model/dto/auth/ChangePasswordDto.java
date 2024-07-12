package com.fleencorp.feen.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.FieldMatch;
import com.fleencorp.base.validator.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldMatch.List({
  @FieldMatch(
    first = "password", second = "confirmPassword", message = "{passwordConfirmation.Equal}")
})
public class ChangePasswordDto {

  @NotBlank(message = "{user.password.NotBlank}")
  @Size(min = 8, max = 24, message = "{user.password.Size}")
  @ValidPassword
  private String password;

  @NotBlank(message = "{user.confirmPassword.NotBlank}")
  @Size(min = 8, max = 24, message = "{user.confirmPassword.Size}")
  @ValidPassword
  @JsonProperty("confirm_password")
  private String confirmPassword;
}
