package com.fleencorp.feen.exception.file;

import com.fleencorp.base.exception.FleenException;

public class FileUploadException extends FleenException {

  private static final String MESSAGE = "An error occurred while uploading a file.";

  public FileUploadException() {
    super(MESSAGE);
  }
}
