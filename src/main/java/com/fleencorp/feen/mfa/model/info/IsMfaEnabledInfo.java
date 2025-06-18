package com.fleencorp.feen.mfa.model.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    return new IsMfaEnabledInfo(mfaEnabled, mfaEnabledText);
  }
}