package com.fleencorp.feen.constant.share;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Represents the block status of an entity.
 * This enum provides statuses that indicate whether an entity is blocked or unblocked.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum BlockStatus implements ApiParameter {

  BLOCKED("Blocked"),
  UNBLOCK("Unblocked");

  private final String value;

  /**
   * Constructs a BlockStatus with the specified string value.
   *
   * @param value the string representation of the block status
   */
  BlockStatus(final String value) {
    this.value = value;
  }
}
