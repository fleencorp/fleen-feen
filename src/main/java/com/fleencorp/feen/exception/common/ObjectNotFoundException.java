package com.fleencorp.feen.exception.common;

import com.fleencorp.feen.exception.base.FleenException;

import java.util.Objects;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;
import static java.lang.String.format;

/**
 * ObjectNotFoundException is a custom exception class that extends FleenException.
 * It is thrown when an object does not exist or cannot be found in the context of AWS S3 Service.
 * This exception is typically used when attempting to retrieve an object from a specific bucket using a key.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public class ObjectNotFoundException extends FleenException {

  public static final String MESSAGE = "Object does not exists or cannot be found. File name: %s";

  public ObjectNotFoundException(final String objectKeyOrFileName) {
    super(format(MESSAGE, Objects.toString(objectKeyOrFileName, UNKNOWN)));
  }
}
