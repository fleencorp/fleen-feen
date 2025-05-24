package com.fleencorp.feen.model.response.security.mfa;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.security.mask.MaskedEmailAddress;
import com.fleencorp.feen.constant.security.mask.MaskedPhoneNumber;
import com.fleencorp.feen.constant.security.mfa.MfaSetupStage;
import com.fleencorp.feen.constant.security.mfa.MfaSetupStatus;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.model.info.security.IsMfaEnabledInfo;
import com.fleencorp.feen.model.info.security.MfaTypeInfo;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Objects.nonNull;

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
  "is_mfa_enabled_info",
  "mfa_type_info",
  "mfa_setup_status",
  "mfa_setup_stage"
})
public class SetupMfaResponse extends LocalizedResponse {

  @JsonProperty("email_address")
  private MaskedEmailAddress emailAddress;

  @JsonProperty("phone_number")
  private MaskedPhoneNumber phoneNumber;

  @JsonProperty("qr_code")
  private String qrCode;

  @JsonProperty("secret")
  private String secret;

  @JsonProperty("is_mfa_enabled_info")
  private IsMfaEnabledInfo mfaEnabledInfo;

  @JsonProperty("mfa_type_info")
  private MfaTypeInfo mfaTypeInfo;

  @JsonFormat(shape = STRING)
  @JsonProperty("mfa_setup_status")
  private MfaSetupStatus mfaSetupStatus;

  @JsonFormat(shape = STRING)
  @JsonProperty("mfa_setup_stage")
  private MfaSetupStage mfaSetupStage;

  @Override
  public String getMessageCode() {
    return "setup.mfa";
  }

  public static SetupMfaResponse of(final String emailAddress, final String phoneNumber, final MfaSetupStatus setupStatus, final IsMfaEnabledInfo isMfaEnabledInfo, final MfaTypeInfo mfaTypeInfo) {
    final MfaSetupStage setupStage = MfaSetupStage.by(nonNull(mfaTypeInfo) ? mfaTypeInfo.getMfaType() : MfaType.NONE);

    return SetupMfaResponse.builder()
      .emailAddress(MaskedEmailAddress.of(emailAddress))
      .phoneNumber(MaskedPhoneNumber.of(phoneNumber))
      .mfaSetupStatus(setupStatus)
      .mfaEnabledInfo(isMfaEnabledInfo)
      .mfaTypeInfo(mfaTypeInfo)
      .mfaSetupStage(setupStage)
      .build();
  }
}
