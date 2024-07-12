package com.fleencorp.feen.event.publisher;

import com.fleencorp.feen.model.request.auth.CompletedUserSignUpRequest;
import com.fleencorp.feen.model.request.auth.ForgotPasswordRequest;
import com.fleencorp.feen.model.request.auth.ResendSignUpVerificationCodeRequest;
import com.fleencorp.feen.model.request.auth.SignUpVerificationRequest;
import com.fleencorp.feen.model.request.mfa.MfaVerificationRequest;
import com.fleencorp.feen.model.request.mfa.ResendMfaVerificationCodeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProfileRequestPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public ProfileRequestPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void sendSignUpVerificationCode(SignUpVerificationRequest signUpVerificationRequest) {
    applicationEventPublisher.publishEvent(signUpVerificationRequest);
  }

  public void sendCompletedSignUpVerification(CompletedUserSignUpRequest completedUserSignUpRequest) {}

  public void resendSignUpVerificationCode(ResendSignUpVerificationCodeRequest resendSignUpVerificationCodeRequest) {}

  public void resendMfaVerificationCode(ResendMfaVerificationCodeRequest resendMfaVerificationCodeRequest) {}

  public void sendMfaVerificationCode(MfaVerificationRequest mfaVerificationRequest) {}

  public void sendForgotPasswordCode(ForgotPasswordRequest forgotPasswordRequest) {}
}
