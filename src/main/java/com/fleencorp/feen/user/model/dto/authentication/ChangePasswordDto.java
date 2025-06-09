package com.fleencorp.feen.user.model.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.FieldMatch;
import com.fleencorp.base.validator.ValidPassword;
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
@FieldMatch.List({
  @FieldMatch(
    first = "password", second = "confirmPassword", message = "{passwordConfirmation.Equal}")
})
public class ChangePasswordDto {

  @NotBlank(message = "{user.password.NotBlank}")
  @Size(min = 8, max = 24, message = "{user.password.Size}")
  @ValidPassword
  @JsonProperty("password")
  private String password;

  @NotBlank(message = "{user.confirmPassword.NotBlank}")
  @Size(min = 8, max = 24, message = "{user.confirmPassword.Size}")
  @ValidPassword
  @JsonProperty("confirm_password")
  private String confirmPassword;
}
