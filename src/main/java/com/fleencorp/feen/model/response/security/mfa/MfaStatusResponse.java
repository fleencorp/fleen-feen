package com.fleencorp.feen.model.response.security.mfa;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "enabled",
  "mfa_type"
})
public class MfaStatusResponse {

  @JsonProperty("enabled")
  private boolean enabled;

  @JsonFormat(shape = STRING)
  @JsonProperty("mfa_type")
  private MfaType mfaType;

  public static MfaStatusResponse of(boolean enabled, MfaType mfaType) {
    return MfaStatusResponse.builder()
        .enabled(enabled)
        .mfaType(mfaType)
        .build();
  }
}
