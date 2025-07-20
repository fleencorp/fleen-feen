package com.fleencorp.feen.common.constant.file;

/**
 * Represents the types of objects that can be uploaded.
 *
 * <p>This enum defines the different categories of objects available for upload within the system,
 * such as profile photos or stream cover photos. Each constant corresponds to a specific
 * type of uploadable object.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public enum ObjectTypeUpload {

  PROFILE_PHOTO,
  STREAM_COVER_PHOTO;

  /**
   * Returns the predefined {@link ObjectTypeUpload} representing a profile photo upload type.
   * This method is used to retrieve the predefined object type for uploading a profile photo.
   *
   * @return The {@link ObjectTypeUpload} representing the profile photo upload type.
   */
  public static ObjectTypeUpload profilePhoto() {
    return PROFILE_PHOTO;
  }

  /**
   * Returns the predefined {@link ObjectTypeUpload} representing a stream cover photo upload type.
   * This method is used to retrieve the predefined object type for uploading a stream cover photo.
   *
   * @return The {@link ObjectTypeUpload} representing the stream cover photo upload type.
   */
  public static ObjectTypeUpload streamCoverPhoto() {
    return STREAM_COVER_PHOTO;
  }

  /**
   * Checks if the provided object type is a profile photo.
   *
   * <p>This method compares the given objectTypeUpload with the predefined PROFILE_PHOTO
   * constant and returns true if they match.</p>
   *
   * @param objectTypeUpload the object type to check
   * @return true if the object type is a profile photo, false otherwise
   */
  public static boolean isProfilePhoto(final ObjectTypeUpload objectTypeUpload) {
    return objectTypeUpload == PROFILE_PHOTO;
  }

  /**
   * Checks if the provided object type is a stream cover photo.
   *
   * <p>This method compares the given objectTypeUpload with the predefined STREAM_COVER_PHOTO
   * constant and returns true if they match.</p>
   *
   * @param objectTypeUpload the object type to check
   * @return true if the object type is a stream cover photo, false otherwise
   */
  public static boolean isStreamCoverPhoto(final ObjectTypeUpload objectTypeUpload) {
    return objectTypeUpload == STREAM_COVER_PHOTO;
  }

}
