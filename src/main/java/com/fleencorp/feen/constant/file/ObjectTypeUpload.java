package com.fleencorp.feen.constant.file;

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

  public static ObjectTypeUpload profilePhoto() {
    return PROFILE_PHOTO;
  }

  public static ObjectTypeUpload streamCoverPhoto() {
    return STREAM_COVER_PHOTO;
  }
}
