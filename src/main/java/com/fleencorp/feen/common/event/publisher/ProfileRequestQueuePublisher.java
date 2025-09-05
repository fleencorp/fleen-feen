package com.fleencorp.feen.common.event.publisher;

import com.fleencorp.feen.common.configuration.external.aws.sqs.SqsQueueNames;
import com.fleencorp.feen.common.event.model.base.PublishMessageRequest;
import com.fleencorp.feen.common.event.service.PublisherService;
import com.fleencorp.feen.mfa.model.request.MfaSetupVerificationRequest;
import com.fleencorp.feen.mfa.model.request.MfaVerificationRequest;
import com.fleencorp.feen.user.model.request.authentication.CompletedUserSignUpRequest;
import com.fleencorp.feen.user.model.request.authentication.ForgotPasswordRequest;
import com.fleencorp.feen.user.model.request.authentication.SignUpVerificationRequest;
import com.fleencorp.feen.user.model.request.profile.ProfileUpdateVerificationRequest;
import com.fleencorp.feen.user.model.request.profile.ResetPasswordSuccessRequest;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

/**
 * Publisher service for publishing profile request messages to SQS queues.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Component
@Primary
@Qualifier("profileRequest-queue")
public class ProfileRequestQueuePublisher implements PublisherService {

  private final SqsQueueNames sqsQueueNames;
  private final SqsTemplate sqsTemplate;

  /**
   * Constructs a ProfileRequestQueuePublisher with the necessary dependencies.
   *
   * @param sqsQueueNames   The names of SQS queues used for profile requests.
   * @param sqsAsyncClient  The asynchronous SQS client used for interacting with SQS.
   */
  public ProfileRequestQueuePublisher(
      final SqsQueueNames sqsQueueNames,
      final SqsAsyncClient sqsAsyncClient) {
    this.sqsQueueNames = sqsQueueNames;
    this.sqsTemplate = SqsTemplate.newTemplate(sqsAsyncClient);
  }

  /**
   * Publishes a message to the appropriate SQS queue based on the message type.
   *
   * @param messageRequest The request containing the message to be published.
   * @see <a href="https://velog.io/@ktf1686/AWS-Amazon-SQS-%EC%A0%81%EC%9A%A9%ED%95%B4%EB%B3%B4%EA%B8%B0-%EC%8A%A4%ED%94%84%EB%A7%81-SQS-%EC%84%A4%EC%A0%95-%EB%B0%8F-AWS-SQS%EB%A1%9C-%EB%A9%94%EC%8B%9C%EC%A7%80-%EB%B3%B4%EB%82%B4%EA%B8%B0-1%ED%8E%B8">
   *   [AWS] Applying Amazon SQS - Setting up Spring SQS and sending messages to AWS SQS</a>
   */
  @Override
  @Transactional
  public void publishMessage(final PublishMessageRequest messageRequest) {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      // Register a synchronization callback to be executed after the transaction commits
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override
        public void afterCommit() {
          // Perform the actual async call after the transaction commits
          final Object message = messageRequest.getMessage();
          processAndDeliverMessage(message);
        }
      });
    }
  }

  /**
   * Deliver and Processes various types of verification messages and sends them to the appropriate SQS queue.
   *
   * @param message the verification message to be processed; it can be of types
   *                {@link SignUpVerificationRequest}, {@link CompletedUserSignUpRequest},
   *                {@link MfaSetupVerificationRequest}, {@link MfaVerificationRequest}, or {@link ForgotPasswordRequest}
   */
  @Async
  public void processAndDeliverMessage(final Object message) {
    switch (message) {
      case final SignUpVerificationRequest request -> sendSignUpVerificationCode(request);
      case final CompletedUserSignUpRequest request -> sendCompletedSignUpVerification(request);
      case final MfaSetupVerificationRequest request -> sendMfaSetupVerificationCode(request);
      case final MfaVerificationRequest request -> sendMfaVerificationCode(request);
      case final ForgotPasswordRequest request -> sendForgotPasswordVerificationCode(request);
      case final ProfileUpdateVerificationRequest request -> sendProfileUpdateVerificationCode(request);
      case final ResetPasswordSuccessRequest request -> sendResetPasswordSuccess(request);
      default -> {}
    }
  }

  /**
   * Sends an MFA verification code by publishing the request to the appropriate SQS queue.
   *
   * @param request the request containing the MFA verification details
   */
  protected void sendMfaVerificationCode(final MfaVerificationRequest request) {
    sqsTemplate.send(to -> to
      .queue(sqsQueueNames.getMfaVerification())
      .payload(request));
  }

  /**
   * Sends an MFA setup verification code by publishing the request to the appropriate SQS queue.
   *
   * @param request the request containing the MFA setup verification details
   */
  protected void sendMfaSetupVerificationCode(final MfaSetupVerificationRequest request) {
    sqsTemplate.send(to -> to
      .queue(sqsQueueNames.getMfaSetup())
      .payload(request));
  }

  /**
   * Sends a completed sign-up verification by publishing the request to the appropriate SQS queue.
   *
   * @param request the request containing the completed user sign-up verification details
   */
  protected void sendCompletedSignUpVerification(final CompletedUserSignUpRequest request) {
    sqsTemplate.send(to -> to
      .queue(sqsQueueNames.getCompleteUserSignUp())
      .payload(request));
  }

  /**
   * Sends a forgot password verification code by publishing the request to the appropriate SQS queue.
   *
   * @param request the request containing the forgot password verification details
   */
  protected void sendForgotPasswordVerificationCode(final ForgotPasswordRequest request) {
    sqsTemplate.send(to -> to
        .queue(sqsQueueNames.getForgotPassword())
        .payload(request));
  }

  /**
   * Sends a sign-up verification code by publishing the request to the appropriate SQS queue.
   *
   * @param request the request containing the sign-up verification details
   */
  protected void sendSignUpVerificationCode(final SignUpVerificationRequest request) {
    sqsTemplate.send(to -> to
      .queue(sqsQueueNames.getSignUpVerification())
      .payload(request));
  }

  /**
   * Publishes a stream event message to the configured SQS queue.
   *
   * @param messageRequest The message request containing the message to be published.
   */
  protected void sendCreateStreamEventMessage(final PublishMessageRequest messageRequest) {
    sqsTemplate.send(to -> to
      .queue(sqsQueueNames.getCreateStreamEvent())
      .payload(messageRequest.getMessage()));
  }

  /**
   * Sends a profile update verification request to the corresponding SQS queue.
   *
   * @param request the profile update verification request to be sent
   */
  protected void sendProfileUpdateVerificationCode(final ProfileUpdateVerificationRequest request) {
    sqsTemplate.send(to -> to
        .queue(sqsQueueNames.getProfileUpdateVerification())
        .payload(request));
  }

  /**
   * Sends a reset password success notification to the corresponding SQS queue.
   *
   * @param request the reset password success request to be sent
   */
  protected void sendResetPasswordSuccess(final ResetPasswordSuccessRequest request) {
    sqsTemplate.send(to -> to
        .queue(sqsQueueNames.getResetPasswordSuccess())
        .payload(request));
  }
}
