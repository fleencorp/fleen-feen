package com.fleencorp.feen.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fleencorp.base.resolver.impl.SearchParamArgResolver;
import org.springframework.context.annotation.*;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static com.fasterxml.jackson.core.json.JsonWriteFeature.ESCAPE_NON_ASCII;
import static com.fasterxml.jackson.databind.DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@Configuration
@ComponentScan(basePackages = {
  "com.fleencorp.feen",
  "com.fleencorp.base"
})
@PropertySources({
  @PropertySource("classpath:properties/aws.properties"),
  @PropertySource("classpath:properties/queue.properties"),
  @PropertySource("classpath:properties/slack.properties"),
  @PropertySource("classpath:properties/token.properties"),
  @PropertySource("classpath:properties/google-service-account.properties"),
  @PropertySource("classpath:properties/google-oauth2-web-client.properties")
})
public class FleenFeenConfiguration implements WebMvcConfigurer {

  private final SearchParamArgResolver queryParamResolver;

  /**
   * Constructs a new instance of {@code FleenFeenConfiguration}.
   *
   * @param queryParamResolver an instance of {@code SearchParamArgResolver} used for resolving
   *                           search parameters in the application. The {@code @Lazy} annotation
   *                           indicates that this dependency is injected lazily, meaning it will
   *                           be created only when it is first needed rather than at application startup.
   */
  public FleenFeenConfiguration(
      @Lazy SearchParamArgResolver queryParamResolver) {
    this.queryParamResolver = queryParamResolver;
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
      .disable(FAIL_ON_EMPTY_BEANS)
      .addModule(new JavaTimeModule())
      .findAndAddModules()
      .build();
  }

  /**
   * Adds a custom argument resolver to the list of argument resolvers.
   *
   * @param resolvers The list of argument resolvers to add the custom resolver to
   */
  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(queryParamResolver);
  }
}
