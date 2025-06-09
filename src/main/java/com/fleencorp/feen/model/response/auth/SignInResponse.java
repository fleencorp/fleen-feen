package com.fleencorp.feen.model.response.auth;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.feen.user.constant.auth.AuthenticationStage;
import com.fleencorp.feen.user.constant.auth.AuthenticationStatus;
import com.fleencorp.feen.constant.security.mask.MaskedEmailAddress;
import com.fleencorp.feen.constant.security.mask.MaskedPhoneNumber;
import com.fleencorp.feen.user.constant.mfa.MfaType;
import com.fleencorp.feen.user.model.info.security.IsMfaEnabledInfo;
import com.fleencorp.feen.user.model.info.security.MfaTypeInfo;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Objects.nonNull;

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
  "mfa_type_info",
  "is_mfa_enabled_info"
})
public class SignInResponse extends LocalizedResponse {

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

  @JsonProperty("mfa_type_info")
  private MfaTypeInfo mfaTypeInfo;

  @JsonProperty("is_mfa_enabled_info")
  private IsMfaEnabledInfo mfaEnabledInfo;

  @Override
  public String getMessageCode() {
    return "sign.in";
  }

  @JsonIgnore
  public String getPreVerificationMessageCode() {
    return "sign.in.pre.verification";
  }

  @JsonIgnore
  public String getMfaAuthenticatorMessageCode() {
    return "sign.in.mfa.authenticator";
  }

  @JsonIgnore
  public MfaType getMfaType() {
    return nonNull(mfaTypeInfo) ? mfaTypeInfo.getMfaType() : null;
  }

  @JsonIgnore
  public String getMfaEmailOrPhoneMessageCode() {
    return MfaType.isEmail(getMfaType())
      ? "sign.in.mfa.email"
      : "sign.in.mfa.phone";
  }

  @JsonIgnore
  public String getMfaMessageCode() {
    return MfaType.isAuthenticator(getMfaType())
      ? getMfaAuthenticatorMessageCode()
      : getMfaEmailOrPhoneMessageCode();
  }

  @Override
  public Object[] getParams() {
    final String verificationType = MfaType.isPhone(getMfaType()) ? phoneNumber.toString() : emailAddress.toString();
    return new Object[] { verificationType };
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

  /**
   * Updates the access token, refresh token, and authentication status.
   *
   * <p>This method sets the new access token and refresh token for the user, as well as
   * updating the authentication status to reflect the current login state.</p>
   *
   * @param accessToken the new access token to be set
   * @param refreshToken the new refresh token to be set
   * @param authenticationStatus the new authentication status to be set
   */
  public void updateTokenAndAuthenticationStatus(final String accessToken, final String refreshToken, final AuthenticationStatus authenticationStatus) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.authenticationStatus = authenticationStatus;
  }

  /**
   * Updates the access token and refresh token.
   *
   * <p>This method sets the provided access token and refresh token to the current instance,
   * allowing for the updating of authentication credentials.</p>
   *
   * @param accessToken the new access token to be set
   * @param refreshToken the new refresh token to be set
   */
  public void updateAccessAndRefreshToken(final String accessToken, final String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
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

  public static SignInResponse ofDefault(final String emailAddress) {
    return SignInResponse.builder()
      .emailAddress(MaskedEmailAddress.of(emailAddress))
      .authenticationStatus(AuthenticationStatus.IN_PROGRESS)
      .authenticationStage(AuthenticationStage.NONE)
      .build();
  }

}
