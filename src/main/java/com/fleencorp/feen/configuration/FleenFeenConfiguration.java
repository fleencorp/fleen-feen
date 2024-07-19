package com.fleencorp.feen.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static com.fasterxml.jackson.core.json.JsonWriteFeature.ESCAPE_NON_ASCII;
import static com.fasterxml.jackson.databind.DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@Configuration
@ComponentScan(basePackages = "com.fleencorp.feen")
public class FleenFeenConfiguration {

  private final String token;

  public FleenFeenConfiguration(@Value("${slack.report.token}") String token) {
    this.token = token;
  }

  /**
  * Provides an ObjectMapper bean.
  *
  * @return ObjectMapper instance configured with various settings
  */
  @Primary
  @Bean
  public ObjectMapper objectMapper() {
    return JsonMapper.builder()
      .configure(ACCEPT_CASE_INSENSITIVE_ENUMS, true)
      .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
      .configure(WRITE_DATES_AS_TIMESTAMPS, false)
      .configure(ACCEPT_CASE_INSENSITIVE_ENUMS, true)
      .configure(ESCAPE_NON_ASCII, false)
      .addModule(new JavaTimeModule())
      .findAndAddModules()
      .disable(FAIL_ON_EMPTY_BEANS)
      .build();
  }

  @Bean
  public Slack slack() {
    return Slack.getInstance();
  }

  @Bean
  public MethodsClient methodsClient() {
    return slack().methods(token);
  }
}
