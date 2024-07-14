package com.fleencorp.feen.model.response.security.mfa;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.security.mfa.MfaSetupStatus;
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
  "email_address",
  "phone_number",
  "qr_code",
  "secret",
  "mfa_enabled",
  "mfa_type",
  "mfa_setup_status"
})
public class SetupMfaResponse {

  @JsonProperty("email_address")
  private String emailAddress;

  @JsonProperty("phone_number")
  private String phoneNumber;

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

}
