package com.fleencorp.feen.constant.review;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enumeration representing different types of reviews.
 *
 * <p>The {@code ReviewType} enum defines the various types of reviews that can be associated with different entities, such as streams.</p>
 *
 * <p>Each enum constant holds a string value that can be used for external communication, such as API parameters.</p>
 *
 * <p>For example, the {@code STREAM} type is used to represent a review for a stream entity.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum ReviewType implements ApiParameter {

  STREAM("Stream");

  private final String value;

  ReviewType(final String value) {
    this.value = value;
  }
}
