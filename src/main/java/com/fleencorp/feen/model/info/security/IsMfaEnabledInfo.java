package com.fleencorp.feen.model.info.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "mfa_enabled",
  "mfa_enabled_text"
})
public class IsMfaEnabledInfo {

  @JsonProperty("mfa_enabled")
  private Boolean mfaEnabled;

  @JsonProperty("mfa_enabled_text")
  private String mfaEnabledText;

  public static IsMfaEnabledInfo of(final Boolean mfaEnabled, final String mfaEnabledText) {
    return IsMfaEnabledInfo.builder()
      .mfaEnabled(mfaEnabled)
      .mfaEnabledText(mfaEnabledText)
      .build();
  }
}