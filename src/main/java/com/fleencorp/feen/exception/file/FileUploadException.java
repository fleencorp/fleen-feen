package com.fleencorp.feen.exception.file;

import com.fleencorp.base.exception.FleenException;

public class FileUploadException extends FleenException {

  @Override
  public String getMessageCode() {
    return "file.upload";
  }
}
