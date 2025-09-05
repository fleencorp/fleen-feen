package com.fleencorp.feen.softask.constant.other;

import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum MoodTag {

  HAPPY("Happy", "mood.tag.happy"),
  SAD("Sad", "mood.tag.sad"),
  EXCITED("Excited", "mood.tag.excited"),
  ANGRY("Angry", "mood.tag.angry"),
  THOUGHTFUL("Thoughtful", "mood.tag.thoughtful"),
  CURIOUS("Curious", "mood.tag.curious"),
  BORED("Bored", "mood.tag.bored"),
  GRATEFUL("Grateful", "mood.tag.grateful"),
  HOPEFUL("Hopeful", "mood.tag.hopeful"),
  CONFUSED("Confused", "mood.tag.confused"),
  RELAXED("Relaxed", "mood.tag.relaxed"),
  INSPIRED("Inspired", "mood.tag.inspired");

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

