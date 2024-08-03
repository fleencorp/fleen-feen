package com.fleencorp.feen.configuration.external.aws.s3;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Configuration properties for AWS S3 bucket names.
 *
 * <p>This class is used to map properties defined in the `aws.properties` file
 * with the prefix "aws.s3.bucket" to corresponding fields. These properties
 * specify the names of various S3 buckets used in the application, such as
 * those for user photos, stream cover photos, and stream recordings.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws.s3.bucket")
@PropertySources({
  @PropertySource("classpath:properties/aws.properties")
})
public class S3BucketNames {

  private String userPhoto;
  private String streamCoverPhoto;
}