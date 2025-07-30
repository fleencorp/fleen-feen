package com.fleencorp.feen.configuration.external.aws.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleencorp.feen.common.converter.TextPlainJsonMessageConverter;
import io.awspring.cloud.sqs.MessageExecutionThreadFactory;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.AcknowledgementOrdering;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.awspring.cloud.sqs.support.converter.SqsMessagingMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.time.Duration;

/**
 * Configuration class for AWS Simple Queue Service (SQS).
 * Sets up necessary configurations for interacting with AWS SQS.
 *
 * @see <a href="https://velog.io/@ktf1686/AWS-Amazon-SQS-%EC%A0%81%EC%9A%A9%ED%95%B4%EB%B3%B4%EA%B8%B0">
 *   [AWS] Applying Amazon SQS - Creating SQS</a>
 * @see <a href="https://velog.io/@ktf1686/Spring-Spring-Batch-Amazon-SQS-%EC%A0%81%EC%9A%A9%ED%95%B4%EB%B3%B4%EA%B8%B0">
 *   [Spring] Applying Spring Batch + Amazon SQS</a>
 * @see <a href="https://kim-jong-hyun.tistory.com/145">Things to keep in mind when using Spring Cloud AWS SQS</a>
 * @see <a href="https://bebong.tistory.com/entry/SQS-with-Spring">SQS with Spring</a>
 * @see <a href="https://uchupura.tistory.com/109">[AWS] Connecting FIFO type AWS SQS using spring-cloud-aws-messaging</a>
 * @see <a href="https://dev-racoon.tistory.com/46">3)SpringBoot SQSListner로 메세지 받기</a>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Configuration
public class AwsSimpleQueueServiceConfig {

  private final AwsCredentialsProvider awsCredentialsProvider;
  private final ObjectMapper objectMapper;
  private final Region region;

  /**
   * Constructs a new configuration for Amazon Simple Queue Service (SQS) using the provided credentials provider and region.
   *
   * @param awsCredentialsProvider The AWS credentials provider used to authenticate with SQS.
   * @param region                 The AWS region where SQS queues are located.
   */
  public AwsSimpleQueueServiceConfig(
      final AwsCredentialsProvider awsCredentialsProvider,
      final ObjectMapper objectMapper,
      @Qualifier("awsRegion") final Region region) {
    this.awsCredentialsProvider = awsCredentialsProvider;
    this.objectMapper = objectMapper;
    this.region = region;
  }

  /**
   * Configures and returns an Asynchronous SQS (Simple Queue Service) client instance.
   * The SQS client allows sending, receiving, and managing messages in SQS queues.
   * @return SqsAsyncClient
   */
  @Bean
  public SqsAsyncClient sqsAsyncClient() {
    return SqsAsyncClient.builder()
        .credentialsProvider(awsCredentialsProvider)
        .region(region)
        .build();
  }

  /**
   * Configures a default {@link SqsMessageListenerContainerFactory} bean that handles
   * SQS message listener containers for processing messages asynchronously from SQS queues.
   *
   * @param sqsAsyncClient the asynchronous client for interacting with SQS
   * @return the configured {@link SqsMessageListenerContainerFactory} bean
   *
   * @see <a href="https://velog.io/@ktf1686/AWS-Amazon-SQS-%EC%A0%81%EC%9A%A9%ED%95%B4%EB%B3%B4%EA%B8%B0-%EC%8A%A4%ED%94%84%EB%A7%81-SQS-%EC%84%A4%EC%A0%95-%EB%B0%8F-AWS-SQS%EB%A1%9C-%EB%A9%94%EC%8B%9C%EC%A7%80-%EB%B3%B4%EB%82%B4%EA%B8%B0-1%ED%8E%B8">
   * [AWS] Applying Amazon SQS - Setting up Spring SQS and sending messages to AWS SQS</a>
   */
  @Bean("defaultSqsListenerContainerFactory")
  public SqsMessageListenerContainerFactory<Object> defaultSqsMessageListenerContainerFactory(final SqsAsyncClient sqsAsyncClient) {
    return SqsMessageListenerContainerFactory.builder()
        .configure(options -> options
            .acknowledgementMode(AcknowledgementMode.ALWAYS)
            .acknowledgementInterval(Duration.ofSeconds(3))
            .acknowledgementThreshold(5)
            .acknowledgementOrdering(AcknowledgementOrdering.ORDERED)
            .messageConverter(messageConverter())
            .componentsTaskExecutor(threadPoolTaskExecutor()))
        .sqsAsyncClient(sqsAsyncClient)
        .build();
  }

  /**
   * Configures a new {@link SqsTemplate} bean for interacting with Amazon SQS using
   * asynchronous client {@link SqsAsyncClient}.
   *
   * @return the configured {@link SqsTemplate} bean
   */
  @Bean
  public SqsTemplate sqsTemplate() {
    return SqsTemplate.newTemplate(sqsAsyncClient());
  }

  /**
   * Creates a bean for converting SQS messages with a specified ObjectMapper.
   *
   * @return A configured {@link SqsMessagingMessageConverter} instance.
   * @see <a href="https://velog.io/@haerong22/Sqs-Listener-Payload-DTO-%EB%B0%94%EC%9D%B8%EB%94%A9%EC%8B%9C-%EC%97%90%EB%9F%AC">
   *   Sqs Listener - Error when binding @Payload DTO</a>
   */
  @Bean
  public SqsMessagingMessageConverter messageConverter() {
/*    MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
    mappingJackson2MessageConverter.setStrictContentTypeMatch(false);
    mappingJackson2MessageConverter.setObjectMapper(objectMapper);
    mappingJackson2MessageConverter.setSerializedPayloadClass(String.class);*/

    final SqsMessagingMessageConverter converter = new SqsMessagingMessageConverter();
    converter.setObjectMapper(objectMapper);
    final TextPlainJsonMessageConverter payloadConverter = new TextPlainJsonMessageConverter(objectMapper);
    converter.setPayloadMessageConverter(payloadConverter);
    return converter;
  }

  /**
   *
   * Configures and provides a ThreadPoolTaskExecutor bean.
   * This executor will handle the execution of tasks with a fixed thread pool size.
   * @return the configured ThreadPoolTaskExecutor
   *
   * @see <a href="https://velog.io/@junghyeon/Spring-Boot-3.0-Migration">Spring 3.0 Migration</a>
   */
  @Bean
  public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setThreadFactory(new MessageExecutionThreadFactory());
    executor.setCorePoolSize(50);
    executor.setMaxPoolSize(50);
    executor.setThreadNamePrefix("sqs-thread-");
    executor.initialize();
    return executor;
  }

}
