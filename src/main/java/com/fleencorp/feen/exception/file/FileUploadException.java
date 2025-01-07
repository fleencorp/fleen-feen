package com.fleencorp.feen.exception.file;

import com.fleencorp.localizer.model.exception.ApiException;

public class FileUploadException extends ApiException {

  @Override
  public String getMessageCode() {
    return "file.upload";
  }
}
