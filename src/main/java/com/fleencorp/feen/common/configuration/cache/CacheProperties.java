package com.fleencorp.feen.common.configuration.cache;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Configuration properties for Redis cache credentials.
 * These properties are typically used to configure a Redis cache connection.</p>
 *
 * <code>jbang springPropertyDocumenter@mikomatic -o generated-docs.md</code>
 *
 * @see
 * <a href="https://www.mortega.dev/posts/spring-boot-property-document-maven-jbang/">Document your Spring Boot properties with Jbang</a>
 */
@Getter
@Setter
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {

  /**
   * Host address of the Redis server.
   */
  @NotBlank
  private String host;

  /**
   * Port number of the Redis server.
   */
  @NotBlank
  private Integer port;

  /**
   * Username for authentication (if required) to the Redis server.
   */
  @NotBlank
  private String username;

  /**
   * Password for authentication (if required) to the Redis server.
   */
  @NotBlank
  private String password;

  /**
   * Prefix to be used for cache keys.
   */
  @NotBlank
  private String prefix;

  /**
   * Time-to-live (TTL) in seconds for cache entries.
   */
  @NotBlank
  private Integer ttl;

  /**
   * Maximum number of idle connections in the Redis connection pool.
   */
  @NotBlank
  private Integer maxIdle;

  /**
   * Maximum number of active connections in the Redis connection pool.
   */
  @NotBlank
  private Integer maxActive;

  /**
   * Maximum total connections allowed in the Redis connection pool.
   */
  @NotBlank
  private Integer maxTotal;
}