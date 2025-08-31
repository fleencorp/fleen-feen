package com.fleencorp.feen.service.impl.external.aws;

import com.fleencorp.feen.common.service.misc.ObjectService;
import com.fleencorp.feen.configuration.external.aws.s3.S3BucketNames;
import com.fleencorp.feen.model.dto.aws.CreateSignedUrlDto;
import com.fleencorp.feen.model.response.external.aws.SignedUrlsResponse;
import com.fleencorp.feen.service.impl.external.aws.s3.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fleencorp.feen.model.response.external.aws.SignedUrlsResponse.SignedUrl;
import static java.util.Objects.nonNull;


/**
 * Implementation of the ObjectService interface.
 *
 * <p>This class provides concrete implementations for the methods defined in the ObjectService interface.
 * It interacts with the necessary repositories, services, and other components to perform the required operations.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Service
public class ObjectServiceImpl implements ObjectService {

  private final StorageService storageService;
  private final S3BucketNames bucketNames;

  /**
   * Constructs an ObjectServiceImpl with the provided S3Service.
   *
   * @param storageService The S3Service implementation.
   */
  public ObjectServiceImpl(
      final StorageService storageService,
      final S3BucketNames bucketNames) {
    this.storageService = storageService;
    this.bucketNames = bucketNames;
  }

  /**
   * Retrieves the file extension from the given filename.
   *
   * <p>This method uses {@link StringUtils#getFilenameExtension} to extract and return
   * the file extension from the provided filename.</p>
   *
   * @param filename the name of the file
   * @return the file extension, or {@code null} if none is found
   */
  protected String getFileExtension(final String filename) {
    return StringUtils.getFilenameExtension(filename);
  }

  /**
   * Removes the file extension from the given filename.
   *
   * <p>This method uses {@link StringUtils#stripFilenameExtension} to strip and return
   * the filename without its extension.</p>
   *
   * @param filename the name of the file
   * @return the filename without the extension, or the original filename if no extension is found
   */
  protected String stripExtension(final String filename) {
    return StringUtils.stripFilenameExtension(filename);
  }

  /**
   * Generates a random name for a file while preserving its original extension.
   *
   * <p>This method retrieves the file extension using {@link #getFileExtension(String)},
   * generates a random object key using {@link StorageService#generateObjectKey(String)}, and
   * concatenates them to create a new filename with the original extension.</p>
   *
   * @param filename the original filename
   * @return the new filename with a random name and the original extension
   */
  protected String generateRandomNameForFile(final String filename) {
    // Get the file extension
    final String fileExt = getFileExtension(filename);

    // Generate a random object key and concatenate with the file extension
    return storageService
            .generateObjectKey(stripExtension(filename))
            .concat(".")
            .concat(nonNull(fileExt) ? fileExt.toLowerCase() : "");
  }

  /**
   * Generates signed URLs for uploading files to cloud storage.
   *
   * <p>This method takes a {@link CreateSignedUrlDto} object containing a list of file names,
   * generates a unique signed URL for each file, and returns a {@link SignedUrlsResponse}
   * containing the signed URLs and associated file metadata. The generated signed URLs
   * allow the client to upload files directly to cloud storage with temporary access.</p>
   *
   * @param createSignedUrlDto the {@link CreateSignedUrlDto} containing the file names
   *                           and the object type to determine the bucket for file upload.
   * @return a {@link SignedUrlsResponse} containing a list of signed URLs, each associated
   *         with a file name, content type, and the URL to upload the file.
   */
  @Override
  public SignedUrlsResponse createSignedUrls(final CreateSignedUrlDto createSignedUrlDto) {
    final List<String> fileNames = createSignedUrlDto.getAllFileNames();
    final List<SignedUrlsResponse.SignedUrl> signedUrls = new ArrayList<>();

    // Iterate over each file name to generate signed URLs.
    for (final String fileName : fileNames) {
      final String generatedFileName = generateRandomNameForFile(fileName);
      final String fileContentType = storageService.detectContentType(generatedFileName);
      final String bucketName = bucketNames.byObjectType(createSignedUrlDto.getObjectType());
      final String url = storageService.generateSignedUrl(bucketName, generatedFileName, fileContentType);
      final SignedUrl signedUrl = SignedUrl.of(url, generatedFileName, fileContentType, fileContentType);

      signedUrls.add(signedUrl);
    }

    return SignedUrlsResponse.of(signedUrls);
  }

  @Override
  public Map<String, String> getAvatarUrls(final String avatarId) {
    return storageService.getAvatarUrls(bucketNames.getSoftAskAvatar(), avatarId);
  }

}
