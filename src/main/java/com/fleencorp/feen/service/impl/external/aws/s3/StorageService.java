package com.fleencorp.feen.service.impl.external.aws.s3;

import com.fleencorp.feen.common.exception.ObjectNotFoundException;
import com.fleencorp.feen.common.exception.file.FileUploadException;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
import com.fleencorp.feen.configuration.external.aws.AwsConfig;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.awscore.presigner.PresignRequest;
import software.amazon.awssdk.awscore.presigner.PresignedRequest;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.time.Instant;
import java.util.*;

import static com.fleencorp.base.util.datetime.DateTimeUtil.getTimeInMillis;
import static com.fleencorp.feen.common.util.common.LoggingUtil.logIfEnabled;
import static java.time.Duration.ofHours;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static software.amazon.awssdk.http.SdkHttpMethod.GET;
import static software.amazon.awssdk.http.SdkHttpMethod.PUT;


/**
 * The S3Service class provides methods for generating signed URLs for accessing objects in Amazon S3.
 * It utilizes an Amazon S3 client and an S3 presigner to generate the signed URLs.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
@Slf4j
public class StorageService {

  private final AwsConfig awsConfig;
  private final S3Client amazonS3;
  private final S3Presigner s3Presigner;
  private final S3Presigner s3PresignerForRead;
  private static final String FILE_NAME_BLACKLISTED_REGEX = "\\W";
  private static final String FILE_NAME_SEPARATOR = "_";

  public StorageService(
      final AwsConfig awsConfig,
      final S3Client amazonS3,
      final S3Presigner s3Presigner,
      @Qualifier("presignerForRead") final S3Presigner s3PresignerForRead) {
    this.awsConfig = awsConfig;
    this.amazonS3 = amazonS3;
    this.s3Presigner = s3Presigner;
    this.s3PresignerForRead = s3PresignerForRead;
  }

  /**
   * Generates a signed URL for accessing the specified object in the given S3 bucket using the provided HTTP method.
   *
   * @param bucketName the name of the S3 bucket containing the object
   * @param fileName the name of the object in the bucket
   * @param httpMethod the HTTP method to be used for accessing the object (e.g., GET, PUT, DELETE)
   * @return a signed URL for accessing the object with the specified HTTP method
   */
  public String generateSignedUrl(final String bucketName, final String fileName, final SdkHttpMethod httpMethod) {
    return generateSignedUrl(bucketName, fileName, httpMethod, 1);
  }

  /**
   * Generates a signed URL with a specific expiration time for accessing the specified object in the given S3 bucket
   * using the provided HTTP method.
   *
   * @param bucketName the name of the S3 bucket containing the object
   * @param fileName the name of the object in the bucket
   * @param httpMethod the HTTP method to be used for accessing the object (e.g., GET, PUT, DELETE)
   * @param hour the number of hours until the signed URL expires
   * @return a signed URL with the specified expiration time for accessing the object with the specified HTTP method
   */
  public String generateSignedUrl(final String bucketName, final String fileName, final SdkHttpMethod httpMethod, final int hour) {
    final Calendar expirationDate = Calendar.getInstance();
    expirationDate.setTime(new Date());
    expirationDate.add(Calendar.HOUR, hour);
    return generateSignedUrl(bucketName, fileName, httpMethod, expirationDate.getTime(), hour);
  }

  /**
   * Generates a signed URL for uploading an object to the specified location in the given S3 bucket.
   *
   * @param bucketName the name of the S3 bucket to upload the object to
   * @param fileName the name of the object to be uploaded
   * @return a signed URL for uploading the object
   */
  public String generateSignedUrl(final String bucketName, final String fileName) {
    return generateSignedUrl(bucketName, fileName, PUT, 1);
  }

  /**
   * Generates a signed URL with additional headers for uploading an object to the specified location in the given S3 bucket.
   *
   * @param bucketName the name of the S3 bucket to upload the object to
   * @param objectKey the key of the object to be uploaded
   * @param contentType the content type of the object
   * @return a signed URL with additional headers for uploading the object
   */
  public String generateSignedUrlWithHeaders(final String bucketName, final String objectKey, final String contentType) {
    return generateSignedUrlWithHeaders(bucketName, objectKey, contentType, PUT, 1);
  }

  /**
   * Generates a signed URL with additional headers and a specific expiration time for uploading an object to the specified location in the given S3 bucket.
   *
   * @param bucketName the name of the S3 bucket to upload the object to
   * @param fileName the name of the object to be uploaded
   * @param contentType the content type of the object
   * @return a signed URL with additional headers and a specific expiration time for uploading the object
   */
  public String generateSignedUrl(final String bucketName, final String fileName, final String contentType) {
    return generateSignedUrlWithHeaders(bucketName, fileName, contentType, PUT, 3);
  }

  /**
   * Generates a signed URL with additional headers and a specific expiration time for accessing or uploading an object to the specified location in the given S3 bucket.
   *
   * @param bucketName the name of the S3 bucket containing the object
   * @param objectKey the key of the object in the bucket
   * @param contentType the content type of the object
   * @param httpMethod the HTTP method to be used for accessing or uploading the object (e.g., GET, PUT)
   * @param hour the number of hours until the signed URL expires
   * @return a signed URL with additional headers and a specific expiration time for accessing or uploading the object
   */
  public String generateSignedUrlWithHeaders(final String bucketName, final String objectKey, final String contentType, final SdkHttpMethod httpMethod, final int hour) {
    S3Request s3Request = null;
    if (httpMethod == PUT) {
      s3Request = PutObjectRequest
        .builder()
        .bucket(bucketName)
        .key(objectKey)
        .contentType(contentType)
        .build();
    } else if (httpMethod == GET) {
      s3Request = GetObjectRequest
        .builder()
        .bucket(bucketName)
        .key(objectKey)
        .responseExpires(getExpirationDate(hour))
        .build();
    }

    final PresignRequest preSignRequest = buildPreSignRequestWithS3Request(s3Request, httpMethod, hour);
    final PresignedRequest preSignedRequest = buildPresignedRequest(preSignRequest, httpMethod);

    return nonNull(preSignedRequest) ? preSignedRequest.url().toString() : null;
  }

  /**
   * Generates a signed URL for downloading an object from the specified location in the given S3 bucket.
   *
   * @param bucketName the name of the S3 bucket containing the object
   * @param objectKey the key of the object in the bucket
   * @return a signed URL for downloading the object
   */
  public String generateDownloadUrl(final String bucketName, final String objectKey) {
    return generateDownloadUrl(bucketName, objectKey, 5);
  }

  /**
   * Generates a signed URL for downloading an object from the specified location in the given S3 bucket with a specific expiration time.
   *
   * @param bucketName the name of the S3 bucket containing the object
   * @param objectKey the key of the object in the bucket
   * @param hour the number of hours until the signed URL expires
   * @return a signed URL for downloading the object with a specific expiration time
   */
  public String generateDownloadUrl(final String bucketName, final String objectKey, final int hour) {
    final GetObjectRequest getObjectRequest = GetObjectRequest.builder()
      .bucket(bucketName)
      .key(objectKey)
      .responseContentDisposition(generateContentDispositionValue(objectKey))
      .responseExpires(getExpirationDate(hour))
      .build();

    final GetObjectPresignRequest objectPresSignedRequest = GetObjectPresignRequest.builder()
      .getObjectRequest(getObjectRequest)
      .signatureDuration(ofHours(hour))
      .build();

    final PresignedGetObjectRequest presignedGetObjectRequest = s3PresignerForRead.presignGetObject(objectPresSignedRequest);
    return presignedGetObjectRequest.url().toString();
  }

  /**
   * Generates a signed URL for accessing or uploading an object in the specified location in the given S3 bucket with a specific expiration date and time.
   *
   * @param bucketName the name of the S3 bucket containing the object
   * @param objectKey the key of the object in the bucket
   * @param httpMethod the HTTP method to be used for accessing or uploading the object (e.g., GET, PUT)
   * @param expirationDate the date and time when the signed URL will expire
   * @param hour the number of hours until the signed URL expires
   * @return a signed URL for accessing or uploading the object with a specific expiration date and time
   *
   * @see <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-s3-presign.html">Work with Amazon S3 pre-signed URLs</a>
   * @see <a href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/example_s3_Scenario_PresignedUrl_section.html">Create a presigned URL for Amazon S3 using an AWS SDK</a>
   */
  public String generateSignedUrl(final String bucketName, final String objectKey, final SdkHttpMethod httpMethod, final Date expirationDate, final int hour) {
    final Instant expiration = getExpirationDate(expirationDate);
    S3Request s3Request = null;

    if (httpMethod == PUT) {
      s3Request = PutObjectRequest
        .builder()
        .bucket(bucketName)
        .key(objectKey)
        .build();
    } else if (httpMethod == GET) {
      s3Request = GetObjectRequest
        .builder()
        .bucket(bucketName)
        .key(objectKey)
        .build();
    }

    final PresignRequest preSignRequest = buildPreSignRequestWithS3Request(s3Request, httpMethod, hour);
    final PresignedRequest preSignedRequest = buildPresignedRequest(preSignRequest, httpMethod);

    return nonNull(preSignedRequest) ? preSignedRequest.url().toString() : null;
  }

  /**
   * Generates a signed URL for accessing an existing object in the specified location of the given S3 bucket.
   *
   * @param bucketName the name of the S3 bucket containing the object
   * @param fileName the name of the existing object in the bucket
   * @return a signed URL for accessing the existing object
   * @throws ObjectNotFoundException if the specified object does not exist in the bucket
   */
  public String getObjectSignedUrl(final String bucketName, final String fileName) {
    if (isObjectExists(bucketName, fileName)) {
      throw new ObjectNotFoundException(fileName);
    }
    return generateSignedUrl(bucketName, fileName, SdkHttpMethod.GET);
  }

  /**
   * Generates a signed URL for uploading an object with a randomly generated file name and the specified extension to the given S3 bucket.
   *
   * @param bucketName the name of the S3 bucket to upload the object to
   * @param extension the file extension to be appended to the randomly generated file name
   * @return a signed URL for uploading the object with the randomly generated file name and the specified extension
   */
  public String generateObjectSignedUrl(final String bucketName, final String extension) {
    final String fileName = UUID.randomUUID() + extension;
    return generateSignedUrl(bucketName, fileName, PUT);
  }

  /**
   * Generates a unique object key by combining a randomly generated UUID and the current system time in milliseconds.
   *
   * @return a unique object key
   */
  public String generateObjectKey() {
    return UUID.randomUUID() + String.valueOf(System.currentTimeMillis());
  }

  /**
   * Extracts the object key from the provided object URL.
   *
   * @param objectUrl the URL of the object in Amazon S3
   * @return the object key extracted from the URL
   */
  public String getObjectKeyFromUrl(@NotNull final String objectUrl) {
    String objectKey = objectUrl.substring(objectUrl.lastIndexOf("/") + 1);
    final int questionMarkIndex = objectKey.lastIndexOf("?");

    if (questionMarkIndex != -1) {
      objectKey = objectKey.substring(0, questionMarkIndex);
    }
    return objectKey;
  }

  /**
   * Constructs a GetObjectRequest for retrieving an object from the specified location in the given S3 bucket.
   *
   * @param bucketName the name of the S3 bucket containing the object
   * @param objectKey the key of the object in the bucket
   * @return a GetObjectRequest for retrieving the specified object
   */
  public GetObjectRequest getObjectRequest(final String bucketName, final String objectKey) {
    return GetObjectRequest.builder()
      .key(objectKey)
      .bucket(bucketName)
      .build();
  }

  /**
   * Deletes the object with the specified key from the specified S3 bucket.
   *
   * @param bucketName the name of the S3 bucket containing the object to be deleted
   * @param objectKey the key of the object to be deleted
   * @return a DeleteResponse indicating the success of the deletion operation
   * @throws ObjectNotFoundException if the specified object does not exist in the bucket
   */
  public FleenFeenResponse.DeleteResponse deleteObject(@NotNull final String bucketName, @NotNull final String objectKey) {
    if (isObjectExists(bucketName, objectKey)) {
      final DeleteObjectRequest objectRequest = DeleteObjectRequest
        .builder()
        .bucket(bucketName)
        .key(objectKey)
        .build();

      amazonS3.deleteObject(objectRequest);
      return FleenFeenResponse.DeleteResponse.of();
    }

    throw new ObjectNotFoundException(objectKey);
  }

  /**
   * Asynchronously deletes the object with the specified key from the specified S3 bucket, suppressing any ObjectNotFoundException that may occur.
   *
   * @param bucketName the name of the S3 bucket containing the object to be deleted
   * @param objectKey the key of the object to be deleted
   */
  @Async
  public void deleteObjectSilent(@NotNull final String bucketName, @NotNull final String objectKey) {
    try {
      deleteObject(bucketName, objectKey);
    } catch (final ObjectNotFoundException _) {}
  }

  /**
   * Deletes multiple objects with the specified keys from the specified S3 bucket.
   *
   * @param bucketName the name of the S3 bucket containing the objects to be deleted
   * @param objectKeys the keys of the objects to be deleted
   */
  public void deleteMultipleObjects(final String bucketName, @NotNull final List<String> objectKeys) {
    final List<ObjectIdentifier> identifiers = new ArrayList<>();
    for (final String key : objectKeys) {
      final ObjectIdentifier objectIdentifier = ObjectIdentifier.builder().key(key).build();
      identifiers.add(objectIdentifier);
    }

    final Delete deleteObject = Delete.builder().objects(identifiers).build();
    final DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest
      .builder()
      .bucket(bucketName)
      .delete(deleteObject)
      .build();

    amazonS3.deleteObjects(deleteObjectsRequest);
  }

  /**
   * Moves an object from the source bucket to the target bucket within Amazon S3.
   *
   * @param bucketSourceName the name of the source bucket containing the object
   * @param objectName the key of the object to be moved
   * @param bucketTargetName the name of the target bucket to move the object to
   */
  public void moveObject(final String bucketSourceName, final String objectName, final String bucketTargetName) {
    final CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
      .sourceBucket(bucketSourceName)
      .sourceKey(objectName)
      .destinationBucket(bucketTargetName)
      .destinationKey(objectName).build();

    amazonS3.copyObject(copyObjectRequest);
  }

  /**
   * Checks whether an object exists in the specified S3 bucket.
   *
   * @param bucketName the name of the S3 bucket to check
   * @param objectKey the key of the object to check
   * @return true if the object exists in the bucket, false otherwise
   */
  public boolean isObjectExists(final String bucketName, final String objectKey) {
    if (nonNull(objectKey)) {
      try {
        final HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
          .bucket(bucketName)
          .key(objectKey)
          .build();
        return amazonS3.headObject(headObjectRequest) != null && amazonS3.headObject(headObjectRequest).lastModified() != null;
      } catch (final S3Exception _) {
        return false;
      }
    }
    return false;
  }

  /**
   * Generates a unique object key based on the current timestamp and the provided object name.
   *
   * @param objectName the name of the object
   * @return a unique object key
   */
  public String generateObjectKey(final String objectName) {
    return (new Date()).getTime() +
            "-" +
            objectName.replaceAll(FILE_NAME_BLACKLISTED_REGEX, FILE_NAME_SEPARATOR);
  }

  /**
   * Retrieves an object stream from the specified bucket in Amazon S3.
   *
   * @param bucketName the name of the S3 bucket containing the object
   * @param filename the name of the object to retrieve
   * @return an object stream representing the requested object
   */
  public Object getObjectStream(final String bucketName, final String filename) {
    final ResponseInputStream<GetObjectResponse> object = getObject(bucketName, filename);
    return ResponseEntity
            .ok()
            .cacheControl(CacheControl.noCache())
            .header(CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE)
            .header(CONTENT_DISPOSITION, generateContentDispositionValue(filename))
            .body(new InputStreamResource(object));
  }

  /**
   * Retrieves an input stream for the specified object from the specified bucket in Amazon S3.
   *
   * @param bucketName the name of the S3 bucket containing the object
   * @param filename the name of the object to retrieve
   * @return an input stream representing the requested object
   */
  public Object getObjectInputStream(final String bucketName, final String filename) {
    final ResponseBytes<GetObjectResponse> objectBytes = amazonS3.getObjectAsBytes(
      GetObjectRequest.builder()
        .bucket(bucketName)
        .key(filename)
        .build()
    );

    return objectBytes.asInputStream();
  }

  /**
   * Retrieves a response input stream for the specified object from the specified bucket in Amazon S3.
   *
   * @param bucketName the name of the S3 bucket containing the object
   * @param objectKey the key of the object to retrieve
   * @return a response input stream representing the requested object, or null if the object does not exist
   */
  public ResponseInputStream<GetObjectResponse> getObject(final String bucketName, final String objectKey) {
    final GetObjectRequest objectRequest = GetObjectRequest
      .builder()
      .key(objectKey)
      .bucket(bucketName)
      .build();

    if (isObjectExists(bucketName, objectKey)) {
      return amazonS3.getObject(objectRequest);
    }
    return null;
  }

  /**
   * Retrieves the metadata of the specified object from the specified bucket in Amazon S3.
   *
   * @param bucketName the name of the S3 bucket containing the object
   * @param objectKey the key of the object to retrieve metadata for
   * @return the metadata of the requested object
   */
  public HeadObjectResponse getObjectMetaData(final String bucketName, final String objectKey) {
    final HeadObjectRequest objectRequest = HeadObjectRequest
      .builder()
      .key(objectKey)
      .bucket(bucketName)
      .build();

    return amazonS3.headObject(objectRequest);
  }


  /**
   * Retrieves the content of the specified object from the specified bucket in Amazon S3 as a byte array.
   *
   * @param bucketName the name of the S3 bucket containing the object
   * @param objectKey the key of the object to retrieve
   * @return a byte array containing the content of the requested object, or null if the object does not exist
   */
  public ResponseBytes<GetObjectResponse> getObjectContent(final String bucketName, final String objectKey) {
    final GetObjectRequest objectRequest = GetObjectRequest
      .builder()
      .key(objectKey)
      .bucket(bucketName)
      .build();

    if (isObjectExists(bucketName, objectKey)) {
      return amazonS3.getObjectAsBytes(objectRequest);
    }
    return null;
  }

  /**
   * Asynchronously uploads a file to the specified bucket in Amazon S3.
   *
   * @param bucketName the name of the S3 bucket to upload the file to
   * @param multipartFile the multipart file to upload
   * @param objectMetaData optional metadata to associate with the uploaded object
   * @throws RuntimeException if an error occurs during the upload process
   */
/*  @Async
  public void uploadObject(final String bucketName, final MultipartFile multipartFile, final Optional<Map<String, String>> objectMetaData) {
    try {
//      final String originalFileName = requireNonNull(multipartFile.getOriginalFilename());
      final String fileName = now()
        .toString()
        .concat(COMMA);
//        .concat(originalFileName);
      final File file = convertMultipartFileToFile(multipartFile, fileName);

      final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .metadata(new HashMap<>())
        .acl(PUBLIC_READ)
        .build();
      final RequestBody body = RequestBody.fromFile(file);

      amazonS3.putObject(putObjectRequest, body);
      Files.delete(file.getAbsoluteFile().toPath());
    }
    catch (final IOException ex) {
      logIfEnabled(log::isErrorEnabled, () -> log.error(ex.getMessage(), ex));
      throw new FileUploadException();
    }
  }*/

  /**
   * Uploads a byte array as an object to the specified bucket in Amazon S3.
   *
   * @param bucketName the name of the S3 bucket to upload the object to
   * @param qrCodeImage the byte array representing the object content
   * @param objectKey the key of the object to upload
   * @param contentType the content type of the object
   * @throws RuntimeException if an error occurs during the upload process
   */
  public void uploadObject(final String bucketName, final byte[] qrCodeImage, final String objectKey, final String contentType) {
    try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(qrCodeImage)) {
      final long contentLength = qrCodeImage.length;
      final PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(objectKey)
        .contentLength(contentLength)
        .contentType(contentType)
        .build();

      final RequestBody body = RequestBody.fromInputStream(inputStream, contentLength);
      amazonS3.putObject(request, body);
    } catch (final IOException ex) {
      logIfEnabled(log::isErrorEnabled, () -> log.error(ex.getMessage(), ex));
      throw new FileUploadException();
    }
  }

/*  *//**
   * Converts a multipart file to a regular File.
   *
   * @param multipartFile the multipart file to convert
   * @param fileName the name of the resulting file
   * @return the converted file
   * @throws RuntimeException if an error occurs during the conversion process
   *//*
  private File convertMultipartFileToFile(final MultipartFile multipartFile, final String fileName) {
    final File file = new File(fileName);

    try (final FileOutputStream outputStream = new FileOutputStream(file)) {
      outputStream.write(multipartFile.getBytes());
    } catch (final IOException ex) {
      logIfEnabled(log::isErrorEnabled, () -> log.error(ex.getMessage(), ex));
      throw new UnableToCompleteOperationException();
    }
    return file;
  }*/

  /**
   * Extracts the base URL from the provided URL.
   *
   * @param url the URL to extract the base URL from
   * @return the base URL
   */
  public String getBaseUrlFromUrl(@NotNull final String url) {
    final int questionMarkIndex = url.indexOf("?");
    if (questionMarkIndex != -1) {
      return url.substring(0, questionMarkIndex);
    } else {
      return url;
    }
  }

  /**
   * Calculates the expiration date based on the specified number of hours.
   *
   * @param hour the number of hours until expiration
   * @return the expiration date as an Instant
   */
  private Instant getExpirationDate(final int hour) {
    final Date expirationDate = new Date();
    long expirationTimeInMillis = expirationDate.getTime();

    expirationTimeInMillis += getTimeInMillis(60, 60, hour, 0);
    expirationDate.setTime(expirationTimeInMillis);

    return expirationDate.toInstant();
  }

  /**
   * Detects the content type based on the provided file name or extension.
   *
   * @param fileNameOrExtension the file name or extension to detect the content type from
   * @return the detected content type, or null if it cannot be determined
   */
  public String detectContentType(final String fileNameOrExtension) {
    return URLConnection.guessContentTypeFromName(fileNameOrExtension);
  }

  /**
   * Generates a content disposition value for the specified object key.
   *
   * @param objectKey the key of the object
   * @return the generated content disposition value
   */
  private String generateContentDispositionValue(final String objectKey) {
    return "attachment; filename=\"" + objectKey + "\"";
  }

  /**
   * Builds a pre-signed request with the provided S3 request, HTTP method, and signature duration.
   *
   * @param s3Request the S3 request to build the pre-signed request from
   * @param httpMethod the HTTP method associated with the request
   * @param signatureDurationInHour the duration of the signature in hours
   * @return the built pre-signed request
   */
  private PresignRequest buildPreSignRequestWithS3Request(final S3Request s3Request, final SdkHttpMethod httpMethod, final int signatureDurationInHour) {
    return switch (httpMethod) {
      case PUT -> PutObjectPresignRequest
        .builder()
        .putObjectRequest((PutObjectRequest) s3Request)
        .signatureDuration(ofHours(signatureDurationInHour))
        .build();
      case GET -> GetObjectPresignRequest
        .builder()
        .getObjectRequest((GetObjectRequest) s3Request)
        .signatureDuration(ofHours(signatureDurationInHour))
        .build();
      default -> null;
    };
  }

  /**
   * Builds a presigned request based on the provided pre-sign request and HTTP method.
   *
   * @param preSignRequest the pre-sign request to build the presigned request from
   * @param httpMethod the HTTP method associated with the request
   * @return the built presigned request
   */
  private PresignedRequest buildPresignedRequest(final PresignRequest preSignRequest, final SdkHttpMethod httpMethod) {
    return switch (httpMethod) {
      case PUT -> s3Presigner.presignPutObject((PutObjectPresignRequest) preSignRequest);
      case GET -> s3PresignerForRead.presignGetObject((GetObjectPresignRequest) preSignRequest);
      default -> null;
    };
  }

  /**
   * Calculates the expiration date based on the provided expiration date.
   * If the expiration date is null, it calculates the expiration date 7 days from the current date.
   *
   * @param expirationDate the expiration date to calculate
   * @return the expiration date as an Instant
   */
  private Instant getExpirationDate(final Date expirationDate) {
    Date newExpirationDate = expirationDate;
    if (isNull(expirationDate)) {
      newExpirationDate = new Date();
      long expirationTimeInMillis = newExpirationDate.getTime();
      expirationTimeInMillis += getTimeInMillis(60, 60, 24, 7);
      newExpirationDate.setTime(expirationTimeInMillis);
    }
    return newExpirationDate.toInstant();
  }

  /**
   * Builds avatar URLs for both PNG and JPG formats.
   *
   * @param bucketName the S3 bucket containing the avatar
   * @param avatarId   the avatar identifier (without extension)
   * @return a map of file extension to full S3 URL
   */
  public Map<String, String> getAvatarUrls(final String bucketName, final String avatarId) {
    final String region = awsConfig.getRegionName();

    final Map<String, String> urls = new HashMap<>();
    urls.put("png", String.format("https://%s.s3.%s.amazonaws.com/%s.png", bucketName, region, avatarId));
    urls.put("jpg", String.format("https://%s.s3.%s.amazonaws.com/%s.jpg", bucketName, region, avatarId));

    return urls;
  }

}
