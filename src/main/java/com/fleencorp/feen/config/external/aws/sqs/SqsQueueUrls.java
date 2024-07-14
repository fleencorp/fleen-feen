package com.fleencorp.feen.config.external.aws.sqs;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Configuration properties for AWS SQS queue URLs.
 * These properties define the URLs of various queues used in an AWS SQS setup.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws.sqs.queue.url")
@PropertySources({
  @PropertySource("classpath:aws.properties")
})
public class SqsQueueUrls {
  
  private String feenEmail;
  private String feenCreateStreamEvent;
}
