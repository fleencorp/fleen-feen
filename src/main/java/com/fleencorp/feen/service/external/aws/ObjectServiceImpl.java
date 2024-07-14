package com.fleencorp.feen.service.external.aws;

import com.fleencorp.feen.service.common.ObjectService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

  private final S3Service s3Service;

  /**
   * Constructs an ObjectServiceImpl with the provided S3Service.
   *
   * @param s3Service The S3Service implementation.
   */
  public ObjectServiceImpl(S3Service s3Service) {
    this.s3Service = s3Service;
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
  @Override
  public String getFileExtension(String filename) {
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
  @Override
  public String stripExtension(String filename) {
    return StringUtils.stripFilenameExtension(filename);
  }


  /**
   * Generates a random name for a file while preserving its original extension.
   *
   * <p>This method retrieves the file extension using {@link #getFileExtension(String)},
   * generates a random object key using {@link S3Service#generateObjectKey(String)}, and
   * concatenates them to create a new filename with the original extension.</p>
   *
   * @param filename the original filename
   * @return the new filename with a random name and the original extension
   */
  @Override
  public String generateRandomNameForFile(String filename) {
    // Get the file extension
    String fileExt = getFileExtension(filename);

    // Generate a random object key and concatenate with the file extension
    return s3Service
            .generateObjectKey(stripExtension(filename))
            .concat(".")
            .concat(nonNull(fileExt) ? fileExt.toLowerCase() : "");
  }
}
