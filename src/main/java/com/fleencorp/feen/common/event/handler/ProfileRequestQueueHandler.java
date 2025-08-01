package com.fleencorp.feen.common.event.handler;

import com.fleencorp.base.util.JsonUtil;
import com.fleencorp.feen.common.constant.base.ReportMessageType;
import com.fleencorp.feen.common.exception.UnableToCompleteOperationException;
import com.fleencorp.feen.mfa.model.request.MfaSetupVerificationRequest;
import com.fleencorp.feen.mfa.model.request.MfaVerificationRequest;
import com.fleencorp.feen.common.model.message.SmsMessage;
import com.fleencorp.feen.chat.space.model.request.external.message.MessageRequest;
import com.fleencorp.feen.common.repository.message.SmsMessageRepository;
import com.fleencorp.feen.common.service.impl.message.TemplateProcessor;
import com.fleencorp.feen.common.service.message.EmailMessageService;
import com.fleencorp.feen.common.service.message.MobileTextService;
import com.fleencorp.feen.common.service.report.ReporterService;
import com.fleencorp.feen.user.model.request.authentication.CompletedUserSignUpRequest;
import com.fleencorp.feen.user.model.request.authentication.ForgotPasswordRequest;
import com.fleencorp.feen.user.model.request.authentication.SignUpVerificationRequest;
import com.fleencorp.feen.user.model.request.profile.ProfileUpdateVerificationRequest;
import com.fleencorp.feen.user.model.request.profile.ResetPasswordSuccessRequest;
import com.fleencorp.feen.verification.constant.VerificationType;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Handles profile request messages received from queues, processing them
 * by sending corresponding email or SMS messages.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@PropertySources({
  @PropertySource("classpath:application.properties"),
  @PropertySource("classpath:properties/queue.properties")
})
@Component
@Slf4j
public class ProfileRequestQueueHandler {

  private final EmailMessageService emailMessageService;
  private final TemplateProcessor templateProcessor;
  private final MobileTextService mobileTextService;
  private final SmsMessageRepository smsMessageRepository;
  private final ReporterService reporterService;
  private final JsonUtil jsonUtil;

  /**
   * Constructs a {@code ProfileRequestQueueHandler} with required services.
   *
   * @param emailMessageService    Service for sending email messages.
   * @param templateProcessor     Processor for handling templates.
   * @param mobileTextService     Service for sending mobile text messages.
   * @param smsMessageRepository  Repository for SMS message templates.
   */
  public ProfileRequestQueueHandler(
      final EmailMessageService emailMessageService,
      final TemplateProcessor templateProcessor,
      final MobileTextService mobileTextService,
      final SmsMessageRepository smsMessageRepository,
      final ReporterService reporterService,
      final JsonUtil jsonUtil) {
    this.emailMessageService = emailMessageService;
    this.templateProcessor = templateProcessor;
    this.mobileTextService = mobileTextService;
    this.smsMessageRepository = smsMessageRepository;
    this.reporterService = reporterService;
    this.jsonUtil = jsonUtil;
  }

  /**
   * Asynchronously handles sending sign-up verification codes based on the verification type (email or phone).
   *
   * @param request the sign-up verification request containing details like verification type and recipient information
   */
  @Async
//  @SqsListener(value = "${queue.sign-up-verification}")
  public void handleSendSignUpVerificationCode(final SignUpVerificationRequest request, final Acknowledgement acknowledgement) {
    final String message = String.format("Sign up verification request: %s", requestToString(request));
    reportMessage(message);

    if (VerificationType.isEmail(request.getVerificationType())) {
      sendEmailMessage(request);
    } else if (VerificationType.isPhone(request.getVerificationType())) {
      sendSmsMessage(request);
    }
    acknowledgement.acknowledge();
  }

  /**
   * Asynchronously sends a completion email for the user sign-up verification.
   *
   * @param request the completed user sign-up request containing email details
   */
  @Async
//  @SqsListener(value = "${queue.complete-user-sign-up}")
  public void handleSendCompletedSignUpVerification(@Payload final CompletedUserSignUpRequest request) {
    final String message = String.format("Completed user sign up request: %s", requestToString(request));
    reportMessage(message);

    sendEmailMessage(request);
  }

  /**
   * Asynchronously handles the forgot password verification code request received from an SQS queue.
   * Depending on the verification type (email or phone), sends the verification code using the appropriate method.
   *
   * @param request the forgot password request containing details such as verification type and recipient information
   */
  @Async
//  @SqsListener(value = "${queue.forgot-password}")
  public void handleSendForgotPasswordVerificationCode(@Payload final ForgotPasswordRequest request) {
    final String message = String.format("Forgot password verification request: %s", requestToString(request));
    reportMessage(message);

    if (VerificationType.isEmail(request.getVerificationType())) {
      sendEmailMessage(request);
    } else if (VerificationType.isPhone(request.getVerificationType())) {
      sendSmsMessage(request);
    }
  }

  /**
   * Asynchronously handles the MFA setup verification code request received from an SQS queue.
   * Depending on the verification type (email or phone), sends the verification code using the appropriate method.
   *
   * @param request the MFA setup verification request containing details such as verification type and recipient information
   */
  @Async
//  @SqsListener(value = "${queue.mfa-setup}")
  public void handleSendMfaSetupVerificationCode(@Payload final MfaSetupVerificationRequest request) {
    final String message = String.format("Mfa Setup verification request: %s", requestToString(request));
    reportMessage(message);

    if (VerificationType.isEmail(request.getVerificationType())) {
      sendEmailMessage(request);
    } else if (VerificationType.isPhone(request.getVerificationType())) {
      sendSmsMessage(request);
    }
  }

  /**
   * Asynchronously handles the MFA verification code request received from an SQS queue.
   * Depending on the verification type (email or phone), sends the verification code using the appropriate method.
   *
   * @param request the MFA verification request containing details such as verification type and recipient information
   */
  @Async
//  @SqsListener(value = "${queue.mfa-verification}")
  public void handleSendMfaVerificationCode(@Payload final MfaVerificationRequest request) {
    final String message = String.format("Mfa verification request: %s", requestToString(request));
    reportMessage(message);

    if (VerificationType.isEmail(request.getVerificationType())) {
      sendEmailMessage(request);
    } else if (VerificationType.isPhone(request.getVerificationType())) {
      sendSmsMessage(request);
    }
  }

  /**
   * Asynchronously handles the profile update verification request received from an SQS queue.
   * Depending on the verification type (email or phone), sends the verification code using the appropriate method.
   *
   * @param request the profile update verification request containing details such as verification type and recipient information
   */
  @Async
//  @SqsListener(value = "${queue.profile-update-verification}")
  public void handleProfileUpdateVerification(@Payload final ProfileUpdateVerificationRequest request) {
    final String message = String.format("Profile update verification request: %s", requestToString(request));
    reportMessage(message);

    if (VerificationType.isEmail(request.getVerificationType())) {
      sendEmailMessage(request);
    } else if (VerificationType.isPhone(request.getVerificationType())) {
      sendSmsMessage(request);
    }
  }

  /**
   * Asynchronously handles the reset password success event received from an SQS queue.
   * Sends an email message to notify the user about the successful password reset.
   *
   * @param request the reset password success request containing details such as email address and message content
   */
  @Async
//  @SqsListener(value = "${queue.reset-password-success}")
  public void handleResetPasswordSuccessful(@Payload final ResetPasswordSuccessRequest request) {
    final String message = String.format("Reset password verification request: %s", requestToString(request));
    reporterService.sendMessage(message, ReportMessageType.PROFILE_VERIFICATION);

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

  /**
   * Reports a message to the profile verification channel.
   *
   * @param message The message to be sent.
   */
  private void reportMessage(final String message) {
    reporterService.sendMessage(message, ReportMessageType.PROFILE_VERIFICATION);
  }

  /**
   * Converts an object to its JSON string representation.
   *
   * @param request The object to be converted.
   * @return The JSON string representation of the object.
   */
  private String requestToString(final Object request) {
    return jsonUtil.convertToString(request);
  }
}
