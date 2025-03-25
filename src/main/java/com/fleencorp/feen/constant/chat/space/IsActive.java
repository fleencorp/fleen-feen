package com.fleencorp.feen.constant.chat.space;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing whether an entity is active or not, with associated message codes
 * for internationalization and API parameters.
 *
 * <p>Each enum constant represents a state of activeness with a corresponding display value
 * and message codes for localization.</p>
 */
@Getter
public enum IsActive implements ApiParameter {

  NO("No", "is.active.no", "is.active.no.2"),
  YES("Yes", "is.active.yes", "is.active.yes.2");

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  IsActive(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static IsActive by(final boolean isActive) {
    return isActive ? YES : NO;
  }
}
