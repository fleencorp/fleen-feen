package com.fleencorp.feen.constant.social;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

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
  UNBLOCKED("Unblocked");

  private final String value;

  /**
   * Constructs a BlockStatus with the specified string value.
   *
   * @param value the string representation of the block status
   */
  BlockStatus(final String value) {
    this.value = value;
  }

  public static BlockStatus of(final String value) {
    return parseEnumOrNull(value, BlockStatus.class);
  }

  /**
   * Checks if the given {@link BlockStatus} represents a blocked state.
   *
   * <p>This method compares the provided {@code blockStatus} with the predefined
   * {@code BLOCKED} status. If the status is equal to {@code BLOCKED}, it returns {@code true},
   * otherwise, it returns {@code false}.
   *
   * @param blockStatus the status to check
   * @return {@code true} if the status is {@code BLOCKED}, otherwise {@code false}
   */
  public static boolean isBlocked(final BlockStatus blockStatus) {
    return blockStatus == BLOCKED;
  }
}
