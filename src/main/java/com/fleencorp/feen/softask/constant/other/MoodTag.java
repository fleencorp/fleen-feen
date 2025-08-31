package com.fleencorp.feen.softask.constant.other;

import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum MoodTag {

  HAPPY("Happy", "moodTag.happy"),
  SAD("Sad", "moodTag.sad"),
  EXCITED("Excited", "moodTag.excited"),
  ANGRY("Angry", "moodTag.angry"),
  THOUGHTFUL("Thoughtful", "moodTag.thoughtful"),
  CURIOUS("Curious", "moodTag.curious"),
  BORED("Bored", "moodTag.bored"),
  GRATEFUL("Grateful", "moodTag.grateful"),
  HOPEFUL("Hopeful", "moodTag.hopeful"),
  CONFUSED("Confused", "moodTag.confused"),
  RELAXED("Relaxed", "moodTag.relaxed"),
  INSPIRED("Inspired", "moodTag.inspired");

  private final String label;
  private final String messageCode;

  MoodTag(final String label, final String messageCode) {
    this.label = label;
    this.messageCode = messageCode;
  }

  public static MoodTag of(final String value) {
    return parseEnumOrNull(value, MoodTag.class);
  }
}

