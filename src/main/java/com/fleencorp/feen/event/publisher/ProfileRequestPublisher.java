package com.fleencorp.feen.event.publisher;

import com.fleencorp.feen.event.model.base.PublishMessageRequest;
import com.fleencorp.feen.event.service.PublisherService;
import com.fleencorp.feen.model.request.auth.CompletedUserSignUpRequest;
import com.fleencorp.feen.model.request.auth.ForgotPasswordRequest;
import com.fleencorp.feen.model.request.auth.SignUpVerificationRequest;
import com.fleencorp.feen.model.request.mfa.MfaSetupVerificationRequest;
import com.fleencorp.feen.model.request.mfa.MfaVerificationRequest;
import com.fleencorp.feen.model.request.profile.ProfileUpdateVerificationRequest;
import com.fleencorp.feen.model.request.profile.ResetPasswordSuccessRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * A service for publishing profile-related requests as application events.
 * This class implements the {@link PublisherService} interface and uses
 * the {@link ApplicationEventPublisher} to publish events.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 *
 * @see <a href="https://curiousjinan.tistory.com/entry/spring-event-thread">
 *   [Spring] Spring Event Thread Operation Principle (Synchronous/Asynchronous)</a>
 */
@Slf4j
@Component
@Qualifier("profileRequest-appEvent")
public class ProfileRequestPublisher implements PublisherService {

  private final ApplicationEventPublisher applicationEventPublisher;

  /**
   * Constructs a new {@code ProfileRequestPublisher} with the specified
   * {@link ApplicationEventPublisher}.
   *
   * @param applicationEventPublisher the event publisher used to publish profile-related events
   */
  public ProfileRequestPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  /**
   * Publishes a message based on the type of the {@link PublishMessageRequest}.
   * It delegates the message to the appropriate method for processing.
   *
   * @param messageRequest the request containing the message to be published.
   */
  @Override
  @Async
  public void publishMessage(PublishMessageRequest messageRequest) {
    Object message = messageRequest.getMessage();
    switch (message) {
      case SignUpVerificationRequest request -> sendSignUpVerificationCode(request);
      case ForgotPasswordRequest request -> sendForgotPasswordVerificationCode(request);
      case CompletedUserSignUpRequest request -> sendCompletedSignUpVerification(request);
      case MfaSetupVerificationRequest request -> sendMfaSetupVerificationCode(request);
      case MfaVerificationRequest request -> sendMfaVerificationCode(request);
      case ProfileUpdateVerificationRequest request -> sendProfileUpdateVerificationCode(request);
      case ResetPasswordSuccessRequest request -> sendResetPasswordSuccessMessage(request);
      default -> {}
    }
  }

  /**
   * Sends a sign-up verification code by publishing the provided
   * {@link SignUpVerificationRequest} event.
   *
   * @param signUpVerificationRequest the request containing the details
   *                                  for sending the sign-up verification code.
   */
  protected void sendSignUpVerificationCode(SignUpVerificationRequest signUpVerificationRequest) {
    applicationEventPublisher.publishEvent(signUpVerificationRequest);
  }

  /**
   * Sends a notification indicating that the user sign-up process is complete
   * by publishing the provided {@link CompletedUserSignUpRequest} event.
   *
   * @param completedUserSignUpRequest the request containing the details
   *                                   for the completed user sign-up.
   */
  protected void sendCompletedSignUpVerification(CompletedUserSignUpRequest completedUserSignUpRequest) {
    applicationEventPublisher.publishEvent(completedUserSignUpRequest);
  }

  /**
   * Sends a Multi-Factor Authentication (MFA) verification code by publishing
   * the provided {@link MfaVerificationRequest} event.
   *
   * @param mfaVerificationRequest the request containing the details for the MFA verification.
   */
  protected void sendMfaVerificationCode(MfaVerificationRequest mfaVerificationRequest) {
    applicationEventPublisher.publishEvent(mfaVerificationRequest);
  }

  /**
   * Sends a Multi-Factor Authentication (MFA) setup verification code by publishing
   * the provided {@link MfaSetupVerificationRequest} event.
   *
   * @param mfaSetupVerificationRequest the request containing the details for the MFA setup verification.
   */
  protected void sendMfaSetupVerificationCode(MfaSetupVerificationRequest mfaSetupVerificationRequest) {
    applicationEventPublisher.publishEvent(mfaSetupVerificationRequest);
  }

  /**
   * Sends a verification code for the forgot password process by publishing
   * the provided {@link ForgotPasswordRequest} event.
   *
   * @param forgotPasswordRequest the request containing the details for the forgot password verification.
   */
  protected void sendForgotPasswordVerificationCode(ForgotPasswordRequest forgotPasswordRequest) {
    applicationEventPublisher.publishEvent(forgotPasswordRequest);
  }

  /**
   * Publishes a profile update verification request event using the application event publisher.
   *
   * @param profileUpdateVerificationRequest the profile update verification request to be published
   */
  protected void sendProfileUpdateVerificationCode(ProfileUpdateVerificationRequest profileUpdateVerificationRequest) {
    applicationEventPublisher.publishEvent(profileUpdateVerificationRequest);
  }

  /**
   * Publishes a reset password success message using the application event publisher.
   *
   * @param resetPasswordSuccessRequest the request containing details of the reset password success
   */
  protected void sendResetPasswordSuccessMessage(ResetPasswordSuccessRequest resetPasswordSuccessRequest) {
    applicationEventPublisher.publishEvent(resetPasswordSuccessRequest);
  }
}