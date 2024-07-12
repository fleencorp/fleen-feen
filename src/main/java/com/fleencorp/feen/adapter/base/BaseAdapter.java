package com.fleencorp.feen.adapter.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleencorp.base.adapter.base.BaseEndpointBlock;
import com.fleencorp.base.constant.base.ApiParameter;
import com.fleencorp.base.constant.base.EndpointBlock;
import com.fleencorp.base.util.security.AuthUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Base adapter class providing common functionality for making HTTP requests to APIs.
 * This class encapsulates common functionality such as initializing REST clients,
 * building URIs, setting headers, and making HTTP calls.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Setter
public class BaseAdapter {

  /**
   * The base URL for the API endpoints.
   */
  @NotBlank
  protected String baseUrl;

  /**
   * The RestTemplate instance used for making HTTP requests.
   */
  protected RestTemplate restTemplate;

  /**
   * The RestClient instance used for making HTTP requests.
   */
  @Autowired
  protected RestClient restClient;

  /**
   * Constructor for BaseAdapter class.
   *
   * @param baseUrl The base URL of the API endpoints
   */
  protected BaseAdapter(String baseUrl) {
    this.baseUrl = baseUrl;
    this.restTemplate = new RestTemplateBuilder()
      .requestFactory(SimpleClientHttpRequestFactory::new).build();
  }

  /**
   * Makes an HTTP call to the specified URI with the given method, headers, body, and response model type.
   *
   * @param uri            The URI to make the HTTP call to
   * @param method         The HTTP method to use for the call
   * @param headers        The headers to include in the request
   * @param body           The body of the request
   * @param responseModel  The type of the response model
   * @return ResponseEntity containing the response data
   */
  public <T> ResponseEntity<T> doCall(@NonNull URI uri, @NonNull HttpMethod method,
                                      @Nullable Map<String, String> headers,
                                      @Nullable Object body, @NonNull Class<T> responseModel) {
    // Log the HTTP call details
    log.info(String.format("HTTP call to url=%s with method=%s and body=%s", uri, method.name(),
      getPayloadBodyAsString(body)));

    // Set request headers
    HttpHeaders requestHeaders = getHeaders();
    if (headers != null) {
      headers.forEach(requestHeaders::add);
    }

    try {
      // Make the HTTP call using the RestClient
      return restClient
        .method(method)
        .uri(uri)
        .headers(newHeaders -> newHeaders.addAll(requestHeaders))
        .body(getPayloadBodyAsString(body))
        .retrieve()
        .toEntity(responseModel);
    } catch (HttpStatusCodeException e) {
      // Handle HTTP status code errors
      log.error(String.format(
        "An error occurred while HTTP call to url=%s with method=%s and body=%s: %s", uri,
        method.name(),
        getPayloadBodyAsString(body), e.getMessage()));
      HttpHeaders errorHeaders = e.getResponseHeaders();
      String errorBody = e.getResponseBodyAsString();

      return ResponseEntity.status(e.getStatusCode())
        .headers(errorHeaders).body((T) errorBody);
    }
  }

  /**
   * Converts the payload body object to a string representation.
   *
   * @param body The payload body object
   * @return The string representation of the payload body
   */
  public static String getPayloadBodyAsString(Object body) {
    String payloadAsString = "";
    if (body instanceof String) {
      payloadAsString = (String) body;
    } else {
      try {
        payloadAsString = new ObjectMapper().writeValueAsString(body);
      } catch (JsonProcessingException ignored) {
      }
    }
    return payloadAsString;
  }

  /**
   * Initializes a UriComponentsBuilder with the base URL and additional endpoint blocks.
   *
   * @param urlBlocks The additional endpoint blocks
   * @return The initialized UriComponentsBuilder
   */
  protected UriComponentsBuilder initUriBuilder(EndpointBlock... urlBlocks) {
    StringBuilder urlBuilder = new StringBuilder(baseUrl);
    for (EndpointBlock block : urlBlocks) {
      if (block != null) {
        urlBuilder.append(block.value());
      }
    }
    return UriComponentsBuilder.fromHttpUrl(urlBuilder.toString());
  }

  /**
   * Retrieves the headers to be included in the HTTP request.
   *
   * @return The HttpHeaders containing the request headers
   */
  protected HttpHeaders getHeaders() {
    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.setContentType(APPLICATION_JSON);
    return requestHeaders;
  }

  /**
   * Constructs an HTTP header with the given authorization value.
   *
   * @param value The authorization value to be included in the header.
   * @return A {@link Map} containing the HTTP header with the authorization value.
   */
  protected Map<String, String> getAuthHeader(String value) {
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.AUTHORIZATION, value);
    return headers;
  }

  /**
   * Constructs an HTTP header with a Bearer token authorization.
   *
   * @param token The Bearer token to be included in the authorization header.
   * @return A {@link Map} containing the HTTP header with the Bearer token authorization.
   */
  protected Map<String, String> getAuthHeaderWithBearerToken(String token) {
    return getAuthHeader(AuthUtil.getBearerToken(token));
  }

  /**
   * Builds an endpoint path variable based on the provided object.
   *
   * @param object The object to append to the base endpoint path.
   * @return An {@link EndpointBlock} representing the constructed endpoint block.
   */
  protected EndpointBlock buildPathVar(Object object) {
    return new BaseEndpointBlock("/" + object);
  }

  /**
   * Builds a URI from the base URL and additional endpoint blocks.
   *
   * @param urlBlocks The additional endpoint blocks
   * @return The built URI
   */
  protected URI buildUri(EndpointBlock... urlBlocks) {
    return initUriBuilder(urlBlocks).build().toUri();
  }

  protected URI buildUri(Map<ApiParameter, String> queryParams, EndpointBlock... urlBlocks) {
    UriComponentsBuilder uriComponentsBuilder = initUriBuilder(urlBlocks);
    queryParams.forEach((k, v) -> uriComponentsBuilder.queryParam(k.getValue(), v));
    return uriComponentsBuilder.build().toUri();
  }
}
