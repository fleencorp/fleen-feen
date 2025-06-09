package com.fleencorp.feen.user.model.dto.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToTitleCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileInfoDto {

  @NotBlank(message = "{user.firstName.NotBlank}")
  @Size(min = 2, max = 50, message = "{user.firstName.Size}")
  @ToTitleCase
  @JsonProperty("first_name")
  private String firstName;

  @NotBlank(message = "{user.lastName.NotBlank}")
  @Size(min = 2, max = 50, message = "{user.lastName.Size}")
  @ToTitleCase
  @JsonProperty("last_name")
  private String lastName;

  @NotNull(message = "{user.country.NotNull}")
  @Size(max = 1000, message = "{user.country.Size}")
  @JsonProperty("country_code")
  private String countryCode;

}
