package com.fleencorp.feen.common.service.impl.message;

import com.fleencorp.feen.common.service.message.MobileTextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.HashMap;
import java.util.Map;

import static com.fleencorp.feen.common.util.LoggingUtil.logIfEnabled;
import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link MobileTextService} interface that sends SMS messages using Amazon SNS.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class MobileTextServiceImpl implements MobileTextService {

  private final String senderId;
  private final String messageType;
  private final SnsClient snsClient;
  private static final String AWS_SNS_SMS_TYPE = "AWS.SNS.SMS.SMSType";
  private static final String AWS_SNS_SMS_SENDER_ID = "AWS.SNS.SMS.SenderID";
  private static final String AWS_SNS_DATA_TYPE = "String";

  /**
   * Constructs an instance of {@code MobileTextServiceImpl} with the provided {@link SnsClient}.
   *
   * @param senderId The Amazon SNS client Sender ID value e.g. Fleen.
   * @param messageType The Amazon SNS message type e.g. transactional.
   * @param snsClient The Amazon SNS client used to send SMS messages.
   */
  public MobileTextServiceImpl(
      @Value("${sms.message.sender-id}") final String senderId,
      @Value("${sms.message.type}") final String messageType,
      final SnsClient snsClient) {
    this.senderId = senderId;
    this.messageType = messageType;
    this.snsClient = snsClient;
  }

  /**
   * Sends an SMS message to a specified phone number using Amazon SNS.
   *
   * @param phoneNumber the recipient's phone number
   * @param message     the message content to be sent
   */
  @Override
  public void sendMessage(final String phoneNumber, final String message) {
    try {
      final Map<String, MessageAttributeValue> smsAttributes =
          new HashMap<>();

      setAttributes(smsAttributes);

      // .withSdkRequestTimeout(timeoutPeriod)
      snsClient.publish(PublishRequest.builder()
          .message(message)
          .phoneNumber(phoneNumber)
          .messageAttributes(smsAttributes)
          .build());
    } catch (final SqsException ex) {
      logIfEnabled(log::isErrorEnabled, () -> log.error(ex.getMessage(), ex));
    }
  }

  /**
   * Sets additional attributes for SMS messages if provided.
   *
   * @param smsAttributes a map containing SMS attributes to be set
   */
  protected void setAttributes(final Map<String, MessageAttributeValue> smsAttributes) {
    if (nonNull(smsAttributes)) {
      smsAttributes.put(AWS_SNS_SMS_TYPE, MessageAttributeValue.builder()
          .stringValue(messageType)
          .dataType(AWS_SNS_DATA_TYPE).build());

      smsAttributes.put(AWS_SNS_SMS_SENDER_ID, MessageAttributeValue.builder()
          .stringValue(senderId)
          .dataType(AWS_SNS_DATA_TYPE).build());
    }
  }
}
