package com.fleencorp.feen.exception.common;

import com.fleencorp.localizer.model.exception.ApiException;

/**
 * ObjectNotFoundException is a custom exception class that extends ApiException.
 * It is thrown when an object does not exist or cannot be found in the context of AWS S3 Service.
 * This exception is typically used when attempting to retrieve an object from a specific bucket using a key.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public class ObjectNotFoundException extends ApiException {

  @Override
  public String getMessageCode() {
    return "object.not.found";
  }

  public ObjectNotFoundException(final Object...params) {
    super(params);
  }
}
