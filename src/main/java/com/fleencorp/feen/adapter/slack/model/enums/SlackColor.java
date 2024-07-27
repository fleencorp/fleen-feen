package com.fleencorp.feen.adapter.slack.model.enums;

import lombok.Getter;

import java.util.concurrent.atomic.LongAccumulator;

@Getter
public enum SlackColor {

  RED( "#ff0000", ""),
  GREEN("#008000", ""),
  ORANGE("#orange", "");

  private final String label;
  private final String colorCode;

  SlackColor(String label, String colorCode) {
    this.label = label;
    this.colorCode = colorCode;
  }
}
