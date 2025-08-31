package com.fleencorp.feen.softask.model.holder;

import com.fleencorp.feen.model.contract.UserHaveOtherDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserOtherDetailHolder implements UserHaveOtherDetail {

  private Double latitude;
  private Double longitude;

  @Override
  public boolean hasLatitudeAndLongitude() {
    return nonNull(latitude) && nonNull(longitude);
  }

  public static UserOtherDetailHolder of(final Double latitude, final Double longitude) {
    return new UserOtherDetailHolder(latitude, longitude);
  }

  public static UserOtherDetailHolder empty() {
    return new UserOtherDetailHolder();
  }
}
