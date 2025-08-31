package com.fleencorp.feen.common.model.response;

import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {

  private Double latitude;
  private Double longitude;

  public static LocationResponse of(final Double latitude, final Double longitude) {
    return new LocationResponse(latitude, longitude);
  }

  public static LocationResponse empty() {
    return new LocationResponse();
  }
}
