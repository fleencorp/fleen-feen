package com.fleencorp.feen.model.response.security.mfa;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.security.IsMfaEnabledInfo;
import com.fleencorp.feen.model.info.security.MfaTypeInfo;
import com.fleencorp.localizer.model.response.LocalizedResponse;
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
  "is_mfa_enabled_info",
  "mfa_type_info"
})
public class MfaStatusResponse extends LocalizedResponse {

  @JsonProperty("is_mfa_enabled_info")
  private IsMfaEnabledInfo mfaEnabledInfo;

  @JsonProperty("mfa_type_info")
  private MfaTypeInfo mfaTypeInfo;

  @Override
  public String getMessageCode() {
    return "mfa.status";
  }

  public static MfaStatusResponse of(final IsMfaEnabledInfo mfaEnabledInfo, final MfaTypeInfo mfaTypeInfo) {
    return new MfaStatusResponse(mfaEnabledInfo, mfaTypeInfo);
  }
}
