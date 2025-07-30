package com.fleencorp.feen.common.exception.file;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class FileUploadException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "file.upload";
  }
}
