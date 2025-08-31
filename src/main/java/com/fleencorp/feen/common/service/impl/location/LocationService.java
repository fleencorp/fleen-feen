package com.fleencorp.feen.common.service.impl.location;

import com.fleencorp.feen.common.model.response.LocationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LocationService {

  private static final Logger log = LoggerFactory.getLogger(LocationService.class);

  private final String ipapiKey;

  private final RestTemplate restTemplate;

  public LocationService(
      @Value("${ipapi.key:}") final String ipapiKey,
      final RestTemplate restTemplate) {
    this.ipapiKey = ipapiKey;
    this.restTemplate = restTemplate;
  }

  /**
   * Retrieves the geographical location information for the given IP address using the ipapi.co service.
   *
   * <p>The method constructs the request URL based on the presence of an API key. If {@code ipapiKey}
   * is available, it is included in the request; otherwise, a standard endpoint is used. The request
   * is executed using {@link RestTemplate} and the response is mapped to a {@link LocationResponse}.</p>
   *
   * <p>If the request fails (e.g., due to network issues or invalid IP address), a warning is logged
   * and an empty {@link LocationResponse} is returned instead of propagating the exception.</p>
   *
   * @param ipAddress the IP address for which to retrieve location data
   * @return a {@link LocationResponse} containing location information, or an empty response if the lookup fails
   */
  public LocationResponse getLocationFromIP(String ipAddress) {
    try {
      String url = ipapiKey != null ?
        "https://ipapi.co/" + ipAddress + "/json/?key=" + ipapiKey :
        "https://ipapi.co/" + ipAddress + "/json/";

      return restTemplate.getForObject(url, LocationResponse.class);
    } catch (Exception e) {
      log.warn("Failed to get location from IP {}: {}", ipAddress, e.getMessage());
    }
    return LocationResponse.empty();
  }
}

