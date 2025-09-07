package com.fleencorp.feen.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.softask.constant.other.MoodTag;
import com.fleencorp.feen.softask.model.holder.UserOtherDetailHolder;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserOtherDetailDto {

  @DecimalMin(value = "-90.0", message = "{user.location.latitude.DecimalMin}")
  @DecimalMax(value = "90.0", message = "{user.location.latitude.DecimalMax}")
  protected Double latitude;

  @DecimalMin(value = "-180.0", message = "{user.location.longitude.DecimalMin}")
  @DecimalMax(value = "180.0", message = "{user.location.longitude.DecimalMax}")
  protected Double longitude;

  @OneOf(enumClass = MoodTag.class, message = "{user.moodTag.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty(value = "mood")
  protected String mood;

  public MoodTag getMood() {
    return MoodTag.of(mood);
  }

  public boolean hasLatitudeAndLongitude() {
    return nonNull(latitude) && nonNull(longitude);
  }

  public UserOtherDetailHolder getUserOtherDetail() {
    if (nonNull(latitude) && nonNull(longitude)) {
      return UserOtherDetailHolder.of(latitude, longitude);
    }

    return UserOtherDetailHolder.empty();
  }
}