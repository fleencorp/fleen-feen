package com.fleencorp.feen.common.constant.location;

import lombok.Getter;

@Getter
public enum DistanceNumericRange {

  DISTANCE_NUMERIC_RANGE_TEXT(
    "Distance Numeric Range",
    "distance.numeric.range.less.than.1.km",
    "distance.numeric.range.1.to.5.km",
    "distance.numeric.range.5.to.10.km",
    "distance.numeric.range.10.to.25.km",
    "distance.numeric.range.25.to.50.km",
    "distance.numeric.range.50.to.100.km",
    "distance.numeric.range.100.plus.km"
  );

  private final String label;
  private final String lessThan1Km;
  private final String _1To5Km;
  private final String _5To10Km;
  private final String _10To25Km;
  private final String _25To50Km;
  private final String _50To100Km;
  private final String _100PlusKm;

  DistanceNumericRange(
      final String label,
      final String lessThan1Km,
      final String _1To5Km,
      final String _5To10Km,
      final String _10To25Km,
      final String _25To50Km,
      final String _50To100Km,
      final String _100PlusKm) {
    this.label = label;
    this.lessThan1Km = lessThan1Km;
    this._1To5Km = _1To5Km;
    this._5To10Km = _5To10Km;
    this._10To25Km = _10To25Km;
    this._25To50Km = _25To50Km;
    this._50To100Km = _50To100Km;
    this._100PlusKm = _100PlusKm;
  }

  private static DistanceNumericRange distanceNumericRange() {
    return DISTANCE_NUMERIC_RANGE_TEXT;
  }

  public static String getDistanceNumericRangeText(final double distance) {
    if (distance < 1) { return "< 1 km"; }
    else if (distance <= 5) { return "1-5 km"; }
    else if (distance <= 10) { return "5-10 km"; }
    else if (distance <= 25) { return "10-25 km"; }
    else if (distance <= 50) { return "25-50 km"; }
    else if (distance <= 100) { return "50-100 km"; }
    else { return "100+ km"; }
  }
  
  public static String getDistanceRangeMessageCode(final double distance) {
    if (distance < 1) { return distanceNumericRange().getLessThan1Km(); }
    else if (distance <= 5) { return distanceNumericRange().get_1To5Km(); }
    else if (distance <= 10) { return distanceNumericRange().get_5To10Km(); }
    else if (distance <= 25) { return distanceNumericRange().get_10To25Km(); }
    else if (distance <= 50) { return distanceNumericRange().get_25To50Km(); }
    else if (distance <= 100) { return distanceNumericRange().get_50To100Km(); }
    else { return distanceNumericRange().get_100PlusKm(); }
  }
}
