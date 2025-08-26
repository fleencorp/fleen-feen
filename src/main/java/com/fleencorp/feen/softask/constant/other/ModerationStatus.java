package com.fleencorp.feen.softask.constant.other;

import lombok.Getter;

@Getter
public enum ModerationStatus {

  /**
   * Content that has been identified as abusive, such as hate speech,
   * harassment, or threatening language.
   */
  ABUSE("Abuse"),

  /**
   * Content that passed moderation checks and is considered acceptable
   * for display without restrictions.
   */
  CLEAN("Clean"),

  /**
   * Content that has been flagged by automated systems or users
   * for potential review by moderators.
   */
  FLAGGED("Flagged"),

  /**
   * Content that has been hidden from general visibility,
   * either temporarily or permanently, by moderation actions.
   */
  HIDDEN("Hidden"),

  /**
   * Content that is classified as spam, such as promotional material,
   * repetitive posts, or deceptive links.
   */
  SPAM("Spam");


  private final String label;

  ModerationStatus(final String label) {
    this.label = label;
  }
}
