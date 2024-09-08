package com.fleencorp.feen.configuration.rest;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for setting up REST adapter.
 * This class provides configurations for creating RestTemplate and RestClient.
 * It encapsulates the setup required for making RESTful API calls.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Configuration
public class RestAdapterConfiguration {

  /**
   * Bean for creating a RestTemplate.
   * This method configures and provides an instance of RestTemplate,
   * which can be used to make HTTP requests.
   *
   * @return RestTemplate instance configured with default settings
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplateBuilder().requestFactory(
      SimpleClientHttpRequestFactory::new).build();
  }

  /**
   * Bean for creating a RestClient.
   * This method creates a RestClient instance using the configured RestTemplate,
   * providing a simplified interface for making RESTful API calls.
   *
   * @return RestClient instance for making RESTful API calls
   */
  @Bean
  public RestClient restClient() {
    return RestClient.create(restTemplate());
  }
}

