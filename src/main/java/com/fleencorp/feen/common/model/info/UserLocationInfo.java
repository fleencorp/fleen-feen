package com.fleencorp.feen.common.model.info;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "has_distance",
  "distance",
  "distance_text",
  "distance_range_text",
  "localized_distance_range_text",
  "distance_numeric_range_text",
  "localized_distance_numeric_range_text",
})
public class UserLocationInfo {

  @JsonProperty("has_distance")
  private Boolean hasDistance;

  @JsonProperty("distance")
  private Double distance;

  @JsonProperty("distance_text")
  private String distanceText;

  @JsonProperty("distance_range_text")
  private String distanceRangeText;

  @JsonProperty("localized_distance_range_text")
  private String localizedDistanceRangeText;

  @JsonProperty("distance_numeric_range_text")
  private String distanceNumericRangeText;

  @JsonProperty("localized_distance_numeric_range_text")
  private String localizedDistanceNumericRangeText;

  @JsonIgnore
  private Double latitude;

  @JsonIgnore
  private Double longitude;

  public boolean hasLatitudeAndLongitude() {
    return nonNull(latitude) && nonNull(longitude);
  }

  public static UserLocationInfo of(final Double latitude, final Double longitude) {
    final UserLocationInfo userLocationInfo = new UserLocationInfo();
    userLocationInfo.setLatitude(latitude);
    userLocationInfo.setLongitude(longitude);
    userLocationInfo.setHasDistance(userLocationInfo.hasLatitudeAndLongitude());

    return userLocationInfo;
  }
}
