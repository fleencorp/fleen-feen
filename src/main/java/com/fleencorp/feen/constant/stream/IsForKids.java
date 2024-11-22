package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Represents whether an item is intended for kids or not.
 *
 * <p>This enum is used to indicate the target audience of an item. It can either be for kids or not.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum IsForKids implements ApiParameter {

  NO("No", "is.for.kids.no"),
  YES("Yes", "is.for.kids.yes");

  private final String value;
  private final String messageCode;

  IsForKids(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  /**
   * Returns the {@link IsForKids} enum corresponding to the given boolean value.
   *
   * <p>If the provided boolean value is {@code true}, the method returns {@link IsForKids#YES}.
   * If the value is {@code false}, it returns {@link IsForKids#NO}.</p>
   *
   * @param forKids the boolean value indicating whether the item is for kids.
   * @return the {@link IsForKids} enum corresponding to the {@code forKids} value.
   */
  public static IsForKids by(final boolean forKids) {
    return forKids ? YES : NO;
  }
}
