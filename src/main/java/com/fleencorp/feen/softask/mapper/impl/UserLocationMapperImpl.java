package com.fleencorp.feen.softask.mapper.impl;

import com.fleencorp.feen.common.constant.location.DistanceNumericRange;
import com.fleencorp.feen.common.constant.location.DistanceRange;
import com.fleencorp.feen.common.model.info.UserLocationInfo;
import com.fleencorp.feen.common.service.location.GeoService;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.model.contract.UserHaveOtherDetail;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.mapper.UserLocationMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class UserLocationMapperImpl extends BaseMapper implements UserLocationMapper {

  private final GeoService geoService;

  public UserLocationMapperImpl(
      final GeoService geoService,
      final MessageSource messageSource) {
    super(messageSource);
    this.geoService = geoService;
  }

  /**
   * Populates the location details of a response object based on the geographic
   * information of the user and the soft ask response.
   *
   * <p>If both {@code response} and {@code userHaveOtherDetail} contain valid latitude
   * and longitude values, this method calculates the distance between the two locations
   * and derives multiple distance-related representations. These include the raw distance,
   * formatted distance text, distance range labels, and their localized equivalents.
   * A {@link UserLocationInfo} object is then created and attached to the response,
   * encapsulating all computed distance details. If either location data is missing,
   * no distance calculations are performed and the response is updated accordingly.</p>
   *
   * @param response the response object whose location details will be set
   * @param userHaveOtherDetail the object containing the user's location information
   */
  @Override
  public void setLocationDetails(final SoftAskCommonResponse response, final UserHaveOtherDetail userHaveOtherDetail) {
    if (nonNull(response) && nonNull(userHaveOtherDetail)) {
      Double distance = null;
      String distanceText = null;
      String distanceRangeText = null;
      String localizedDistanceRangeText = null;
      String distanceNumericRangeText = null;
      String localizedDistanceNumericRangeText = null;
      final boolean hasLocationData = userHaveOtherDetail.hasLatitudeAndLongitude() && response.hasLatitudeAndLongitude();

      if (hasLocationData) {
        distance = geoService.calculateDistance(
          userHaveOtherDetail.getLatitude(),
          userHaveOtherDetail.getLongitude(),
          response.getUserLocationInfo().getLatitude(),
          response.getUserLocationInfo().getLongitude()
        );

        distanceText = geoService.formatDistance(distance);

        distanceRangeText = DistanceRange.getDistanceRangeText(distance);
        final String distanceRangeMessageCode = DistanceRange.getDistanceRangeMessageCode(distance);
        localizedDistanceRangeText = translate(distanceRangeMessageCode);

        distanceNumericRangeText = DistanceNumericRange.getDistanceNumericRangeText(distance);
        final String distanceNumericRangeMessageCode = DistanceNumericRange.getDistanceRangeMessageCode(distance);
        localizedDistanceNumericRangeText = translate(distanceNumericRangeMessageCode);
      }

      final UserLocationInfo userLocationInfo = new UserLocationInfo();
      userLocationInfo.setHasDistance(hasLocationData);
      userLocationInfo.setDistance(distance);
      userLocationInfo.setDistanceText(distanceText);
      userLocationInfo.setDistanceRangeText(distanceRangeText);
      userLocationInfo.setDistanceNumericRangeText(distanceNumericRangeText);

      userLocationInfo.setLocalizedDistanceRangeText(localizedDistanceRangeText);
      userLocationInfo.setLocalizedDistanceNumericRangeText(localizedDistanceNumericRangeText);
      response.setUserLocationInfo(userLocationInfo);
    }
  }
}
