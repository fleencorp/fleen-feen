package com.fleencorp.feen.event.handler;

import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.exception.stream.UnableToCompleteOperationException;
import com.fleencorp.feen.model.message.SmsMessage;
import com.fleencorp.feen.model.request.auth.CompletedUserSignUpRequest;
import com.fleencorp.feen.model.request.auth.ForgotPasswordRequest;
import com.fleencorp.feen.model.request.auth.SignUpVerificationRequest;
import com.fleencorp.feen.model.request.message.MessageRequest;
import com.fleencorp.feen.model.request.mfa.MfaSetupVerificationRequest;
import com.fleencorp.feen.model.request.mfa.MfaVerificationRequest;
import com.fleencorp.feen.model.request.profile.ProfileUpdateVerificationRequest;
import com.fleencorp.feen.model.request.profile.ResetPasswordSuccessRequest;
import com.fleencorp.feen.repository.message.SmsMessageRepository;
import com.fleencorp.feen.service.impl.message.TemplateProcessor;
import com.fleencorp.feen.service.message.EmailMessageService;
import com.fleencorp.feen.service.message.MobileTextService;
import io.awspring.cloud.sqs.annotation.SqsListener;
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
  @PropertySource("classpath:queue.properties")
})
@Component
@Slf4j
public class ProfileRequestQueueHandler {

  private final EmailMessageService emailMessageService;
  private final TemplateProcessor templateProcessor;
  private final MobileTextService mobileTextService;
  private final SmsMessageRepository smsMessageRepository;

  /**
   * Constructs a {@code ProfileRequestQueueHandler} with required services.
   *
   * @param emailMessageService    Service for sending email messages.
   * @param templateProcessor     Processor for handling templates.
   * @param mobileTextService     Service for sending mobile text messages.
   * @param smsMessageRepository  Repository for SMS message templates.
   */
  public ProfileRequestQueueHandler(
      EmailMessageService emailMessageService,
      TemplateProcessor templateProcessor,
      MobileTextService mobileTextService,
      SmsMessageRepository smsMessageRepository) {
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
  @Async
  @SqsListener(value = "${queue.sign-up-verification}")
  public void handleSendSignUpVerificationCode(SignUpVerificationRequest request, Acknowledgement acknowledgement) {
    if (request.getVerificationType() == VerificationType.EMAIL) {
      sendEmailMessage(request);
    } else if (request.getVerificationType() == VerificationType.PHONE) {
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
  @SqsListener(value = "${queue.complete-user-sign-up}")
  public void handleSendCompletedSignUpVerification(@Payload CompletedUserSignUpRequest request) {
    sendEmailMessage(request);
  }

  /**
   * Asynchronously handles the forgot password verification code request received from an SQS queue.
   * Depending on the verification type (email or phone), sends the verification code using the appropriate method.
   *
   * @param request the forgot password request containing details such as verification type and recipient information
   */
  @Async
  @SqsListener(value = "${queue.forgot-password}")
  public void handleSendForgotPasswordVerificationCode(@Payload ForgotPasswordRequest request) {
    if (request.getVerificationType() == VerificationType.EMAIL) {
      sendEmailMessage(request);
    } else if (request.getVerificationType() == VerificationType.PHONE) {
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
  @SqsListener(value = "${queue.mfa-setup}")
  public void handleSendMfaSetupVerificationCode(@Payload MfaSetupVerificationRequest request) {
    if (request.getVerificationType() == VerificationType.EMAIL) {
      sendEmailMessage(request);
    } else if (request.getVerificationType() == VerificationType.PHONE) {
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
  @SqsListener(value = "${queue.mfa-verification}")
  public void handleSendMfaVerificationCode(@Payload MfaVerificationRequest request) {
    if (request.getVerificationType() == VerificationType.EMAIL) {
      sendEmailMessage(request);
    } else if (request.getVerificationType() == VerificationType.PHONE) {
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
  @SqsListener(value = "${queue.profile-update-verification}")
  public void handleProfileUpdateVerification(@Payload ProfileUpdateVerificationRequest request) {
    if (request.getVerificationType() == VerificationType.EMAIL) {
      sendEmailMessage(request);
    } else if (request.getVerificationType() == VerificationType.PHONE) {
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
  @SqsListener(value = "${queue.reset-password-success}")
  public void handleResetPasswordSuccessful(@Payload ResetPasswordSuccessRequest request) {
    sendEmailMessage(request);
  }


  /**
   * Processes the email message template and sends the email to the specified recipient.
   *
   * @param request the message request containing template name, email address, and message title
   */
  private void sendEmailMessage(MessageRequest request) {
    String messageBody = templateProcessor.processTemplate(
        request.getTemplateName(),
        request.toMessagePayload());
    emailMessageService.sendMessage(request.getEmailAddress(), request.getMessageTitle(), messageBody);
  }

  /**
   * Processes the SMS message template and sends the SMS to the specified phone number.
   *
   * @param request the message request containing template name, phone number, and message payload
   */
  private void sendSmsMessage(MessageRequest request) {
    SmsMessage smsMessage = smsMessageRepository.findByTitle(request.getTemplateName())
        .orElseThrow(UnableToCompleteOperationException::new);
    String messageBody = templateProcessor.processTemplateSms(smsMessage.body(), request.toMessagePayload());
    mobileTextService.sendMessage(request.getPhoneNumber(), messageBody);
  }
}
