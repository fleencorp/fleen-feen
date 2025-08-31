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
  private final String lessThanOneKm;
  private final String oneToFiveKm;
  private final String fiveToTenKm;
  private final String tenToTwentyFiveKm;
  private final String twentyFiveToFiftyKm;
  private final String fiftyToOneHundredKm;
  private final String oneHundredPlusKm;

  DistanceNumericRange(
      final String label,
      final String lessThanOneKm,
      final String oneToFiveKm,
      final String fiveToTenKm,
      final String tenToTwentyFiveKm,
      final String twentyFiveToFiftyKm,
      final String fiftyToOneHundredKm,
      final String oneHundredPlusKm) {
    this.label = label;
    this.lessThanOneKm = lessThanOneKm;
    this.oneToFiveKm = oneToFiveKm;
    this.fiveToTenKm = fiveToTenKm;
    this.tenToTwentyFiveKm = tenToTwentyFiveKm;
    this.twentyFiveToFiftyKm = twentyFiveToFiftyKm;
    this.fiftyToOneHundredKm = fiftyToOneHundredKm;
    this.oneHundredPlusKm = oneHundredPlusKm;
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
    if (distance < 1) {
      return distanceNumericRange().getLessThanOneKm();
    } else if (distance <= 5) {
      return distanceNumericRange().getOneToFiveKm();
    } else if (distance <= 10) {
      return distanceNumericRange().getFiveToTenKm();
    } else if (distance <= 25) {
      return distanceNumericRange().getTenToTwentyFiveKm();
    } else if (distance <= 50) {
      return distanceNumericRange().getTwentyFiveToFiftyKm();
    } else if (distance <= 100) {
      return distanceNumericRange().getFiftyToOneHundredKm();
    } else {
      return distanceNumericRange().getOneHundredPlusKm();
    }
  }

}
