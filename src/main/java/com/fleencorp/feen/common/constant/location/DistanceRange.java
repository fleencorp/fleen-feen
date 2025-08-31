package com.fleencorp.feen.common.constant.location;

import lombok.Getter;

@Getter
public enum DistanceRange {

  DISTANCE_RANGE_TEXT(
    "Distance Range",
    "distance.range.very.close",
    "distance.range.near.by",
    "distance.range.far.way",
    "distance.range.very.far.away"
  );

  private final String label;
  private final String farAway;
  private final String nearBy;
  private final String veryClose;
  private final String veryFarAway;

  DistanceRange(
      final String label,
      final String nearBy,
      final String veryClose,
      final String farAway,
      final String messageCode4) {
    this.label = label;
    this.nearBy = nearBy;
    this.veryClose = veryClose;
    this.farAway = farAway;
    this.veryFarAway = messageCode4;
  }

  private static DistanceRange distanceRange() {
    return DistanceRange.DISTANCE_RANGE_TEXT;
  }

  public static String getDistanceRangeText(final double distance) {
    if (distance < 1) { return "Very Close"; }
    else if (distance < 10) { return "Nearby"; }
    else if (distance < 100) { return "Far Away"; }
    else { return "Very Far Away"; }
  }

  public static String getDistanceRangeMessageCode(final double distance) {
    if (distance < 1) { return distanceRange().getVeryClose(); }
    else if (distance < 10) { return distanceRange().getNearBy(); }
    else if (distance < 100) { return distanceRange().getFarAway(); }
    else { return distanceRange().getVeryFarAway(); }
  }
}
