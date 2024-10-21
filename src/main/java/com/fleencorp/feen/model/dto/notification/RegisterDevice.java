package com.fleencorp.feen.model.dto.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterDevice {

  @NotBlank(message = "device.token.NotBlank")
  @JsonProperty("device_token")
  private String deviceToken;
}
