package com.fleencorp.feen.common.service.impl.location;

import com.fleencorp.feen.common.service.location.GeoService;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class GeoServiceImpl implements GeoService {

  private static final double EARTH_RADIUS_KM = 6371;
  private final DecimalFormat df = new DecimalFormat("#.##");

  // Geohash encoding constants
  private static final String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";

  /**
   * Generates all possible prefix substrings of a given geohash.
   *
   * <p>If the input geohash is not {@code null}, this method creates a list of substrings
   * where each element is a prefix of the geohash starting from the first character
   * up to the current index. For example, given a geohash "abcd", the returned list
   * will contain ["a", "ab", "abc", "abcd"]. If the geohash is {@code null}, an
   * empty list is returned.</p>
   *
   * @param geohash the geohash string from which prefixes are derived
   * @return a list of prefix substrings, or an empty list if the input is {@code null}
   */
  @Override
  public String encodeAndGetGeohash(final Double lat, final Double lon, final int precision) {
    if (lat == null || lon == null || lat < -90.0 || lat > 90.0 || lon < -180.0 || lon > 180.0) {
      return null; // Invalid coordinates
    }

    final double[] latInterval = {-90.0, 90.0};
    final double[] lonInterval = {-180.0, 180.0};
    final StringBuilder geohash = new StringBuilder();
    boolean isEven = true;
    int bit = 0;
    int ch = 0;

    while (geohash.length() < precision) {
      final double[] interval = isEven ? lonInterval : latInterval;
      final double mid = (interval[0] + interval[1]) / 2.0;
      final double coord = isEven ? lon : lat;

      if (coord > mid) {
        ch |= (1 << (4 - bit));
        interval[0] = mid;
      } else {
        interval[1] = mid;
      }

      isEven = !isEven;
      bit++;

      if (bit == 5) {
        geohash.append(BASE32.charAt(ch));
        bit = 0;
        ch = 0;
      }
    }
    return geohash.toString();
  }

  /**
   * Generates all prefix substrings of the given geohash, starting from length one up to the full length,
   * or returns an empty list if the geohash is null.
   *
   * @param geohash the geohash string
   * @return a list of geohash prefixes in ascending length order, or an empty list if the input is null
   */
  public List<String> getGeoPrefixes(final String geohash) {
    final List<String> prefixes = new ArrayList<>();
    if (geohash != null) {
      for (int i = 1; i <= geohash.length(); i++) {
        prefixes.add(geohash.substring(0, i));
      }
    }
    return prefixes;
  }

  /**
   * Returns the prefix of a geohash up to the specified length, or null if the geohash is null,
   * shorter than the requested length, or if the length is less than one.
   *
   * @param geohash the geohash string
   * @param length the desired prefix length
   * @return the geohash prefix of the given length, or null if the input is invalid
   */
  @Override
  public String getGeohashPrefix(final String geohash, final int length) {
    if (geohash == null || geohash.length() < length || length < 1) {
      return null;
    }

    return geohash.substring(0, length);
  }

  /**
   * Calculates the great-circle distance in kilometers between two geographical
   * points specified by their latitude and longitude using the haversine formula.
   *
   * @param lat1 the latitude of the first point in degrees
   * @param lon1 the longitude of the first point in degrees
   * @param lat2 the latitude of the second point in degrees
   * @param lon2 the longitude of the second point in degrees
   * @return the distance in kilometers between the two points
   */
  @Override
  public double calculateDistance(double lat1, final double lon1, double lat2, final double lon2) {
    final double dLat = Math.toRadians(lat2 - lat1);
    final double dLon = Math.toRadians(lon2 - lon1);
    lat1 = Math.toRadians(lat1);
    lat2 = Math.toRadians(lat2);
    final double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
    final double c = 2 * Math.asin(Math.sqrt(a));

    return EARTH_RADIUS_KM * c;
  }

  /**
   * Formats a distance value to a string with "km away".
   */
  @Override
  public String formatDistance(final double distance) {
    return df.format(distance) + " km away";
  }
}
