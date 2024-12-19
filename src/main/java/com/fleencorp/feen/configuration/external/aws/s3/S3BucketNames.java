package com.fleencorp.feen.configuration.external.aws.s3;


import com.fleencorp.feen.constant.file.ObjectTypeUpload;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "aws.s3.bucket")
@PropertySources({
  @PropertySource("classpath:properties/aws.properties")
})
public class S3BucketNames {

  private String userPhoto;
  private String streamCoverPhoto;

  /**
   * Returns the corresponding photo URL or path based on the provided {@link ObjectTypeUpload}.
   *
   * <p>This method maps the given {@link ObjectTypeUpload} to the appropriate photo type. If the object type
   * is {@code PROFILE_PHOTO}, it returns the user's profile photo. If the object type is
   * {@code STREAM_COVER_PHOTO}, it returns the stream's cover photo. If no specific case matches,
   * it defaults to returning the user's profile photo.</p>
   *
   * @param objectTypeUpload the {@link ObjectTypeUpload} specifying the type of object (profile or stream cover photo).
   * @return the URL or path to the corresponding photo, either the user's profile photo or the stream cover photo.
   */
  public String byObjectType(final ObjectTypeUpload objectTypeUpload) {
    switch (objectTypeUpload) {
      case PROFILE_PHOTO -> {
        return userPhoto;
      }
      case STREAM_COVER_PHOTO -> {
        return streamCoverPhoto;
      }
      default -> {}
    }
    return userPhoto;
  }
}