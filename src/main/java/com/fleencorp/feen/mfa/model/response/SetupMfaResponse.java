package com.fleencorp.feen.mfa.model.response;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.feen.common.constant.mask.MaskedEmailAddress;
import com.fleencorp.feen.common.constant.mask.MaskedPhoneNumber;
import com.fleencorp.feen.mfa.constant.MfaSetupStage;
import com.fleencorp.feen.mfa.constant.MfaSetupStatus;
import com.fleencorp.feen.mfa.constant.MfaType;
import com.fleencorp.feen.mfa.model.info.IsMfaEnabledInfo;
import com.fleencorp.feen.mfa.model.info.MfaTypeInfo;
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
  @JsonIgnore
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
