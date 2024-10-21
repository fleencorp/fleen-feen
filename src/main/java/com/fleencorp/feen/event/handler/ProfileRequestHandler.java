package com.fleencorp.feen.event.handler;

import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.exception.base.UnableToCompleteOperationException;
import com.fleencorp.feen.model.message.SmsMessage;
import com.fleencorp.feen.model.request.auth.CompletedUserSignUpRequest;
import com.fleencorp.feen.model.request.auth.ForgotPasswordRequest;
import com.fleencorp.feen.model.request.auth.SignUpVerificationRequest;
import com.fleencorp.feen.model.request.message.MessageRequest;
import com.fleencorp.feen.model.request.mfa.MfaSetupVerificationRequest;
import com.fleencorp.feen.model.request.mfa.MfaVerificationRequest;
import com.fleencorp.feen.model.request.profile.ProfileUpdateSuccessRequest;
import com.fleencorp.feen.model.request.profile.ProfileUpdateVerificationRequest;
import com.fleencorp.feen.model.request.profile.ResetPasswordSuccessRequest;
import com.fleencorp.feen.repository.message.SmsMessageRepository;
import com.fleencorp.feen.service.impl.message.TemplateProcessor;
import com.fleencorp.feen.service.message.EmailMessageService;
import com.fleencorp.feen.service.message.MobileTextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

/**
 * Handles profile request messages, processing them by sending corresponding email or SMS messages.
 */
@Component
@Slf4j
public class ProfileRequestHandler {

  private final EmailMessageService emailMessageService;
  private final TemplateProcessor templateProcessor;
  private final MobileTextService mobileTextService;
  private final SmsMessageRepository smsMessageRepository;

  /**
   * Constructs a {@code ProfileRequestHandler} with required services.
   *
   * @param emailMessageService    Service for sending email messages.
   * @param templateProcessor     Processor for handling templates.
   * @param mobileTextService     Service for sending mobile text messages.
   * @param smsMessageRepository  Repository for SMS message templates.
   */
  public ProfileRequestHandler(
      final EmailMessageService emailMessageService,
      final TemplateProcessor templateProcessor,
      final MobileTextService mobileTextService,
      final SmsMessageRepository smsMessageRepository) {
    this.emailMessageService = emailMessageService;
    this.templateProcessor = templateProcessor;
    this.mobileTextService = mobileTextService;
    this.smsMessageRepository = smsMessageRepository;
  }

  /**
   * Asynchronously handles sending sign-up verification codes based on the verification type (email or phone).
   *
   * @param request the sign-up verification request containing details like verification type and recipient information
   */
  @TransactionalEventListener(phase = AFTER_COMMIT)
  @Async
  public void handleSendSignUpVerificationCode(final SignUpVerificationRequest request) {
    if (VerificationType.isEmail(request.getVerificationType())) {
      sendEmailMessage(request);
    } else if (VerificationType.isPhone(request.getVerificationType())) {
      sendSmsMessage(request);
    }
  }

  /**
   * Asynchronously sends a completion email for the user sign-up verification.
   *
   * @param request the completed user sign-up request containing email details
   */
  @TransactionalEventListener(phase = AFTER_COMMIT)
  @Async
  public void handleSendCompletedSignUpVerification(final CompletedUserSignUpRequest request) {
    sendEmailMessage(request);
  }

  /**
   * Asynchronously handles sending MFA verification codes via email or SMS based on the verification type.
   *
   * @param request the MFA verification request containing the verification type and contact information
   */
  @TransactionalEventListener(phase = AFTER_COMMIT)
  @Async
  public void handleSendMfaVerificationCode(final MfaVerificationRequest request) {
    if (VerificationType.isEmail(request.getVerificationType())) {
      sendEmailMessage(request);
    } else if (VerificationType.isPhone(request.getVerificationType())) {
      sendSmsMessage(request);
    }
  }

  /**
   * Asynchronously handles sending MFA setup verification codes via email or SMS based on the verification type.
   *
   * @param request the MFA setup verification request containing the verification type and contact information
   */
  @TransactionalEventListener(phase = AFTER_COMMIT)
  @Async
  public void handleSendMfaSetupVerificationCode(final MfaSetupVerificationRequest request) {
    if (VerificationType.isEmail(request.getVerificationType())) {
      sendEmailMessage(request);
    } else if (VerificationType.isPhone(request.getVerificationType())) {
      sendSmsMessage(request);
    }
  }

  /**
   * Asynchronously handles sending forgot password verification codes via email or SMS based on the verification type.
   *
   * @param request the forgot password verification request containing the verification type and contact information
   */
  @TransactionalEventListener(phase = AFTER_COMMIT)
  @Async
  public void handleSendForgotPasswordVerificationCode(final ForgotPasswordRequest request) {
    if (VerificationType.isEmail(request.getVerificationType())) {
      sendEmailMessage(request);
    } else if (VerificationType.isPhone(request.getVerificationType())) {
      sendSmsMessage(request);
    }
  }

  /**
   * Asynchronously handles sending profile update verification codes via email or SMS based on the verification type.
   *
   * @param request the profile update verification request containing the verification type and contact information
   */
  @TransactionalEventListener(phase = AFTER_COMMIT)
  @Async
  public void handleSendProfileUpdateCode(final ProfileUpdateVerificationRequest request) {
    if (VerificationType.isEmail(request.getVerificationType())) {
      sendEmailMessage(request);
    } else if (VerificationType.isPhone(request.getVerificationType())) {
      sendSmsMessage(request);
    }
  }

  /**
   * Asynchronously handles sending an email message upon successful profile update.
   *
   * @param request the message request containing details for sending the email
   */
  @TransactionalEventListener(phase = AFTER_COMMIT)
  @Async
  public void handleProfileUpdateSuccessful(final ProfileUpdateSuccessRequest request) {
    sendEmailMessage(request);
  }

  /**
   * Asynchronously handles sending an email message upon successful password reset.
   *
   * @param request the message request containing details for sending the email
   */
  @TransactionalEventListener(phase = AFTER_COMMIT)
  @Async
  public void handleResetPasswordSuccessful(final ResetPasswordSuccessRequest request) {
    sendEmailMessage(request);
  }

  /**
   * Processes the email message template and sends the email to the specified recipient.
   *
   * @param request the message request containing template name, email address, and message title
   */
  private void sendEmailMessage(final MessageRequest request) {
    final String messageBody = templateProcessor.processTemplate(
      request.getTemplateName(),
      request.toMessagePayload());
    emailMessageService.sendMessage(request.getEmailAddress(), request.getMessageTitle(), messageBody);
  }

  /**
   * Processes the SMS message template and sends the SMS to the specified phone number.
   *
   * @param request the message request containing template name, phone number, and message payload
   */
  private void sendSmsMessage(final MessageRequest request) {
    final SmsMessage smsMessage = smsMessageRepository.findByTitle(request.getTemplateName())
        .orElseThrow(UnableToCompleteOperationException::new);
    final String messageBody = templateProcessor.processTemplateSms(smsMessage.body(), request.toMessagePayload());
    mobileTextService.sendMessage(request.getPhoneNumber(), messageBody);
  }
}
