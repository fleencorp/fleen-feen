package com.fleencorp.feen.config.cache;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Slf4j
@Configuration
public class CacheConfiguration {

  private final CacheCredentials credentials;
  private final ObjectMapper mapper;

  public CacheConfiguration(CacheCredentials credentials,
                            ObjectMapper mapper) {
    this.credentials = credentials;
    this.mapper = mapper;
  }

  @Bean
  public JedisConnectionFactory connectionFactory() {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
    configuration.setHostName(credentials.getHost());
    configuration.setPort(credentials.getPort());
    configuration.setPassword(RedisPassword.of(credentials.getPassword()));
    configuration.setUsername(credentials.getUsername());

    return new JedisConnectionFactory(configuration);
  }

  @Bean
  public RedisCacheConfiguration redisCacheConfiguration() {
    RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
    configuration.entryTtl(Duration.ofMinutes(credentials.getTtl()));
    configuration.disableCachingNullValues();
    configuration.usePrefix();
    configuration.prefixCacheNameWith(credentials.getPrefix());

    return configuration;
  }

  @Bean
  public StringRedisSerializer stringSerializer() {
        return new StringRedisSerializer();
    }

  @Bean
  public GenericJackson2JsonRedisSerializer jackson2JsonSerializer() {
    return new GenericJackson2JsonRedisSerializer(mapper);
  }

  @Bean
  public JdkSerializationRedisSerializer jdkSerializer() {
    return new JdkSerializationRedisSerializer();
  }

  @Bean
  @Primary
  public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    configurePool(connectionFactory);
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(stringSerializer());
    template.setValueSerializer(jackson2JsonSerializer());
    template.setHashValueSerializer(jdkSerializer());

    return template;
  }

  private void configurePool(JedisConnectionFactory connectionFactory) {
    if (connectionFactory.getPoolConfig() != null) {
      connectionFactory.getPoolConfig().setMaxTotal(credentials.getMaxTotal());
      connectionFactory.getPoolConfig().setMaxIdle(credentials.getMaxIdle());
    }
  }

  @Bean
  public RedisCacheManager cacheManager() {
    return RedisCacheManager
            .builder(connectionFactory())
            .cacheDefaults(redisCacheConfiguration())
            .transactionAware()
            .build();
  }
}
