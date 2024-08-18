package com.fleencorp.feen.model.response.security.mfa;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.model.response.base.ApiResponse;
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
public class MfaStatusResponse extends ApiResponse {

  @JsonProperty("enabled")
  private boolean enabled;

  @JsonFormat(shape = STRING)
  @JsonProperty("mfa_type")
  private MfaType mfaType;

  @Override
  public String getMessageKey() {
    return "mfa.status";
  }

  public static MfaStatusResponse of(final boolean enabled, final MfaType mfaType) {
    return MfaStatusResponse.builder()
        .enabled(enabled)
        .mfaType(mfaType)
        .build();
  }
}
