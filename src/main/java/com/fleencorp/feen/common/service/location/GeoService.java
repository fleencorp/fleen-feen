package com.fleencorp.feen.common.service.location;

public interface GeoService {

  String encodeAndGetGeohash(Double lat, Double lon, int precision);

  String getGeohashPrefix(String geohash, int length);

  double calculateDistance(double lat1, double lon1, double lat2, double lon2);

  String formatDistance(double distance);
}
