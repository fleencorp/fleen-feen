package com.fleencorp.feen.model.response.security.mfa;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.info.security.IsMfaEnabledInfo;
import com.fleencorp.feen.model.info.security.MfaTypeInfo;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "is_mfa_enabled_info",
  "mfa_type_info"
})
public class MfaStatusResponse extends ApiResponse {

  @JsonProperty("is_mfa_enabled_info")
  private IsMfaEnabledInfo isMfaEnabledInfo;

  @JsonProperty("mfa_type_info")
  private MfaTypeInfo mfaTypeInfo;

  @Override
  public String getMessageCode() {
    return "mfa.status";
  }

  public static MfaStatusResponse of(final IsMfaEnabledInfo mfaEnabledInfo, final MfaTypeInfo mfaTypeInfo) {
    return MfaStatusResponse.builder()
      .isMfaEnabledInfo(mfaEnabledInfo)
      .mfaTypeInfo(mfaTypeInfo)
      .build();
  }
}
