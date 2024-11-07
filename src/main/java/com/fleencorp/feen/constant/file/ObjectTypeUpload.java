package com.fleencorp.feen.constant.file;

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
