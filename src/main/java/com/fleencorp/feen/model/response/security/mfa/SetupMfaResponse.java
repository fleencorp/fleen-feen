package com.fleencorp.feen.model.response.security.mfa;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.security.mask.MaskedEmailAddress;
import com.fleencorp.feen.constant.security.mask.MaskedPhoneNumber;
import com.fleencorp.feen.constant.security.mfa.MfaSetupStatus;
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
  "email_address",
  "phone_number",
  "qr_code",
  "secret",
  "mfa_enabled",
  "mfa_type",
  "mfa_setup_status"
})
public class SetupMfaResponse extends ApiResponse {

  @JsonProperty("email_address")
  private MaskedEmailAddress emailAddress;

  @JsonProperty("phone_number")
  private MaskedPhoneNumber phoneNumber;

  @JsonProperty("qr_code")
  private String qrCode;

  private String secret;

  @JsonProperty("mfa_enabled")
  private boolean enabled;

  @JsonFormat(shape = STRING)
  @JsonProperty("mfa_type")
  private MfaType mfaType;

  @JsonFormat(shape = STRING)
  @JsonProperty("mfa_setup_status")
  private MfaSetupStatus mfaSetupStatus;

  @Override
  public String getMessageKey() {
    return "setup.mfa";
  }

  public static SetupMfaResponse of(final String emailAddress, final String phoneNumber, final MfaSetupStatus setupStatus, final MfaType mfaType, final boolean enabled) {
    return SetupMfaResponse.builder()
      .emailAddress(MaskedEmailAddress.of(emailAddress))
      .phoneNumber(MaskedPhoneNumber.of(phoneNumber))
      .mfaSetupStatus(setupStatus)
      .mfaType(mfaType)
      .enabled(enabled)
      .build();
  }

  public void enabled() {
    this.enabled = true;
  }
}
