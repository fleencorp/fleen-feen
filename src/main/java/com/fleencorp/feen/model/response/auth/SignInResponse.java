package com.fleencorp.feen.model.response.auth;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.constant.security.auth.AuthenticationStage;
import com.fleencorp.feen.constant.security.auth.AuthenticationStatus;
import com.fleencorp.feen.constant.security.mask.MaskedEmailAddress;
import com.fleencorp.feen.constant.security.mask.MaskedPhoneNumber;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "access_token",
  "refresh_token",
  "email_address",
  "phone_number",
  "authentication_status",
  "authentication_stage",
  "mfa_type",
  "mfa_enabled"
})
public class SignInResponse extends ApiResponse {

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

  @JsonFormat(shape = STRING)
  @JsonProperty("mfa_type")
  private MfaType mfaType;

  @JsonProperty("mfa_enabled")
  private Boolean mfaEnabled;

  @Override
  public String getMessageCode() {
    return "sign.in";
  }

  public static SignInResponse of(final String accessToken, final String refreshToken) {
    return of(accessToken, refreshToken, AuthenticationStatus.COMPLETED);
  }

  public static SignInResponse of(final String accessToken, final String refreshToken, final AuthenticationStatus authenticationStatus) {
    return SignInResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .authenticationStatus(authenticationStatus)
        .build();
  }

  public static SignInResponse createDefault(final String emailAddress) {
    return SignInResponse.builder()
        .emailAddress(MaskedEmailAddress.of(emailAddress))
        .authenticationStatus(AuthenticationStatus.IN_PROGRESS)
        .authenticationStage(AuthenticationStage.NONE)
        .mfaEnabled(false)
        .build();
  }

  /**
   * Updates the access token, refresh token, and phone number details.
   *
   * <p>This method sets the provided access token, refresh token, and phone number to the respective fields.
   * The phone number is masked using the {@link MaskedPhoneNumber#of(String)} method before being assigned.</p>
   *
   * @param accessToken the new access token.
   * @param refreshToken the new refresh token.
   * @param phoneNumber the phone number to be updated. It is masked before being assigned.
   */
  public void updateDetails(final String accessToken, final String refreshToken, final String phoneNumber) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.phoneNumber = MaskedPhoneNumber.of(phoneNumber);
  }

  /**
   * Updates the email address and phone number details.
   *
   * <p>This method sets the provided email address and phone number to the respective fields.
   * Both the email address and phone number are masked using the {@link MaskedEmailAddress#of(String)}
   * and {@link MaskedPhoneNumber#of(String)} methods before being assigned.</p>
   *
   * @param emailAddress the email address to be updated. It is masked before being assigned.
   * @param phoneNumber the phone number to be updated. It is masked before being assigned.
   */
  public void updateEmailAndPhone(final String emailAddress, final String phoneNumber) {
    this.emailAddress = MaskedEmailAddress.of(emailAddress);
    this.phoneNumber = MaskedPhoneNumber.of(phoneNumber);
  }

}
