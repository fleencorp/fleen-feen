package com.fleencorp.feen.model.response.auth;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.constant.security.auth.AuthenticationStage;
import com.fleencorp.feen.constant.security.auth.AuthenticationStatus;
import com.fleencorp.feen.constant.security.mask.MaskedEmailAddress;
import com.fleencorp.feen.constant.security.mask.MaskedPhoneNumber;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.model.info.security.VerificationTypeInfo;
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
  "access_token",
  "refresh_token",
  "email_address",
  "phone_number",
  "authentication_status",
  "authentication_stage",
  "verification_type_info"
})
public class SignUpResponse extends ApiResponse {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("refresh_token")
  private String refreshToken;

  @JsonFormat(shape = STRING)
  @JsonProperty("email_address")
  private MaskedEmailAddress emailAddress;

  @JsonFormat(shape = STRING)
  @JsonProperty("phone_number")
  private MaskedPhoneNumber phoneNumber;

  @JsonFormat(shape = STRING)
  @JsonProperty("authentication_status")
  private AuthenticationStatus authenticationStatus;

  @JsonFormat(shape = STRING)
  @JsonProperty("authentication_stage")
  private AuthenticationStage authenticationStage;

  @JsonProperty("verification_type_info")
  private VerificationTypeInfo verificationTypeInfo;

  @JsonIgnore
  private VerificationType getVerificationType() {
    return nonNull(verificationTypeInfo) ? verificationTypeInfo.getVerificationType() : null;
  }

  @Override
  public String getMessageCode() {
    return VerificationType.isEmail(getVerificationType())
      ? "sign.up.email"
      : "sign.up.phone";
  }

  @JsonIgnore
  public String getCompletedSignUpMessageCode() {
    return "sign.up.completed";
  }

  @Override
  public Object[] getParams() {
    return VerificationType.isEmail(getVerificationType())
      ? new Object[]{ emailAddress.getRawValue() }
      : new Object[]{ phoneNumber.getRawValue() };
  }

  public static SignUpResponse of(final String accessToken, final String refreshToken, final String emailAddress, final String phoneNumber,
      final AuthenticationStatus authenticationStatus, final AuthenticationStage authenticationStage) {
    return SignUpResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .emailAddress(MaskedEmailAddress.of(emailAddress))
        .phoneNumber(MaskedPhoneNumber.of(phoneNumber))
        .authenticationStatus(authenticationStatus)
        .authenticationStage(authenticationStage)
        .build();
  }

  public static SignUpResponse of(final String accessToken, final String refreshToken) {
    return of(accessToken, refreshToken, AuthenticationStatus.COMPLETED);
  }

  public static SignUpResponse of(final String accessToken, final String refreshToken, final AuthenticationStatus authenticationStatus) {
    return SignUpResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .authenticationStatus(authenticationStatus)
        .emailAddress(null)
        .phoneNumber(null)
        .build();
  }

  public static SignUpResponse ofDefault(final String accessToken, final String refreshToken, final String emailAddress, final String phoneNumber) {
    return SignUpResponse.of(
      accessToken, refreshToken, emailAddress, phoneNumber, AuthenticationStatus.IN_PROGRESS, AuthenticationStage.PRE_VERIFICATION);
  }
}
