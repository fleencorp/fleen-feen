package com.fleencorp.feen.configuration.external.aws;

import io.awspring.cloud.ses.SimpleEmailServiceJavaMailSender;
import io.awspring.cloud.ses.SimpleEmailServiceMailSender;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sns.SnsClient;

import static software.amazon.awssdk.transfer.s3.SizeConstant.MB;

/**
 * Configuration class for setting up AWS clients and related dependencies.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Configuration
@PropertySources({
  @PropertySource("classpath:aws.properties")
})
public class AwsConfig {

  private final String accessKeyId;
  private final String accessKeySecret;
  private final String regionName;

  /**
   * Constructs an AwsConfig instance with the provided AWS access key ID,
   * access key secret, and region name.
   * @param accessKeyId AWS access key ID
   * @param accessKeySecret AWS access key secret
   * @param regionName AWS region name
   */
  public AwsConfig(
      @Value("${aws.access.key.id}") final String accessKeyId,
      @Value("${aws.access.key.secret}") final String accessKeySecret,
      @Value("${aws.s3.region.name}") final String regionName) {
    this.accessKeyId = accessKeyId;
    this.accessKeySecret = accessKeySecret;
    this.regionName = regionName;
  }

  /**
   * Retrieves the AWS credentials provider.
   * @return AwsCredentialsProvider
   */
  @Bean
  public AwsCredentialsProvider getAwsCredentialsProvider() {
    final AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, accessKeySecret);
    return StaticCredentialsProvider.create(credentials);
  }

  /**
   * Configures and returns an S3Client instance.
   * @return S3Client
   */
  @Bean
  @Primary
  public S3Client getS3Client() {
    return S3Client.builder()
      .credentialsProvider(getAwsCredentialsProvider())
      .region(region())
      .build();
  }

  /**
   * Configures and returns an Async S3Client instance.
   * @return S3Client
   */
  @Bean
  @Primary
  public S3AsyncClient getAsyncS3Client() {
    return S3AsyncClient.crtBuilder()
      .credentialsProvider(getAwsCredentialsProvider())
      .region(region())
      .maxConcurrency(64)
      .targetThroughputInGbps(20.0)
      .minimumPartSizeInBytes(8 * MB)
      .accelerate(true)
      .build();
  }

  /**
   * Configures and returns an SES (Simple Email Service) client instance.
   * The SES client allows sending and receiving email messages using Amazon SES.
   * @return SesClient
   */
  @Bean
  public SesClient emailService() {
    return SesClient.builder()
      .credentialsProvider(getAwsCredentialsProvider())
      .region(region())
      .build();
  }

  /**
   * Configures and returns an SNS (Simple Notification Service) client instance.
   * The SNS client allows sending SMS messages and managing other types of notifications.
   * @return SnsClient
   */
  @Bean
  public SnsClient smsService() {
    return SnsClient.builder()
      .credentialsProvider(getAwsCredentialsProvider())
      .region(region())
      .build();
  }

  /**
   * Configures and returns an S3Presigner instance.
   * The S3 presigner allows generating pre-signed URLs for accessing S3 resources securely.
   * @return S3Presigner
   */
  @Bean
  @Primary
  public S3Presigner s3Presigner() {
    final S3Configuration s3Config = S3Configuration.builder()
      .chunkedEncodingEnabled(true) // Enable chunked encoding for faster uploads
      .accelerateModeEnabled(true)
      .build();
    return S3Presigner.builder()
      .credentialsProvider(getAwsCredentialsProvider())
      .region(region())
      .build();
  }

  @Bean
  @Qualifier("presignerForRead")
  public S3Presigner s3PresignerForRead() {
    return S3Presigner.builder()
      .credentialsProvider(getAwsCredentialsProvider())
      .region(region())
      .build();
  }

  /**
   * Configures and returns the AWS region.
   * @return Region
   */
  @Bean
  @Qualifier("awsRegion")
  public Region region() {
    return Region.of(regionName);
  }

  /**
   * Configures and returns a MailSender instance.
   * The MailSender interface represents a strategy for sending emails.
   * This method returns a SimpleEmailServiceMailSender instance, which is a specific implementation
   * that uses Amazon SES (Simple Email Service) as the email sending service.
   * @return MailSender
   */
  @Bean
  public MailSender mailSender() {
    return new SimpleEmailServiceMailSender(emailService());
  }

  /**
   * Configures and returns a JavaMailSender instance.
   * The JavaMailSender interface is the primary interface for sending emails using JavaMail.
   * This method returns a SimpleEmailServiceJavaMailSender instance, which is a specific implementation
   * that uses Amazon SES (Simple Email Service) as the email sending service.
   * @return JavaMailSender
   */
  @Bean
  public JavaMailSender javaMailSender() {
    return new SimpleEmailServiceJavaMailSender(emailService());
  }
}
