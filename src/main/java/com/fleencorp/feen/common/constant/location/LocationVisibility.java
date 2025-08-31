package com.fleencorp.feen.common.constant.location;

import lombok.Getter;

@Getter
public enum LocationVisibility {

  COUNTRY("Country"), // Show within same country
  GLOBAL("Global"), // Show to everyone
  LOCAL("Local"), // Show within ~5km radius
  NEARBY("Nearby"), // Show within ~25km radius
  PRIVATE("Private"), // Don't show based on location
  REGION("Region"); // Show within ~100km radius

  private final String label;

  LocationVisibility(final String label) {
    this.label = label;
  }
}
