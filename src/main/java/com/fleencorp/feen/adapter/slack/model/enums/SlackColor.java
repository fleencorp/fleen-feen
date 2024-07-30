package com.fleencorp.feen.adapter.slack.model.enums;

import lombok.Getter;

/**
 * The {@code SlackColor} enum represents a set of predefined colors
 * that can be used in Slack messages.
 *
 * <p>Each color is associated with a specific hexadecimal color code
 * and an optional label.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum SlackColor {

  BLUE("#0000FF", ""),
  RED( "#ff0000", ""),
  GREEN("#008000", ""),
  ORANGE("#FFA500", "");

  private final String label;
  private final String colorCode;

  /**
   * Constructs a new {@code SlackColor} with the specified color code and label.
   *
   * @param colorCode the hexadecimal color code associated with the color
   * @param label an optional label for the color
   */
  SlackColor(final String label, final String colorCode) {
    this.label = label;
    this.colorCode = colorCode;
  }
}
