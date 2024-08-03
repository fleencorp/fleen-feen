package com.fleencorp.feen.configuration.external.aws.sqs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Configuration properties for AWS SQS queue names.
 * These properties define the names of various queues used in an AWS SQS setup.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "queue")
@PropertySources({
  @PropertySource("classpath:properties/queue.properties")
})
public class SqsQueueNames {
  
  private String signUpVerification;
  private String forgotPassword;
  private String completeUserSignUp;
  private String mfaSetup;
  private String mfaVerification;
  private String createStreamEvent;
  private String profileUpdateVerification;
  private String resetPasswordSuccess;
}
