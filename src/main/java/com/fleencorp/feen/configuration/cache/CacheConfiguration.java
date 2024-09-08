package com.fleencorp.feen.configuration.cache;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleencorp.feen.event.subscriber.StreamEventSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Configuration class for setting up Redis cache-related beans.

 * This class configures the Redis connection, cache manager, and serializers.
 * It uses {@link CacheProperties} for Redis connection details and {@link ObjectMapper} for JSON serialization.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Configuration
public class CacheConfiguration {

  private final CacheProperties credentials;
  private final ObjectMapper mapper;

  /**
   * Constructs a {@link CacheConfiguration} with the specified {@link CacheProperties} and {@link ObjectMapper}.
   *
   * @param credentials the cache properties containing Redis connection details, such as host, port, and credentials.
   * @param mapper      the {@link ObjectMapper} used for JSON serialization and deserialization.
   */
  public CacheConfiguration(final CacheProperties credentials,
                            final ObjectMapper mapper) {
    this.credentials = credentials;
    this.mapper = mapper;
  }

  /**
   * Creates a {@link JedisConnectionFactory} bean for connecting to a Redis instance.
   *
   * <p>This method sets up the connection configuration for a standalone Redis server using the provided credentials.
   * It includes the hostname, port, username, and password for authenticating with the Redis server.</p>
   *
   * @return a configured {@link JedisConnectionFactory} instance
   * @see <a href="https://velog.io/@wy9295/Redispub-sub">
   *   Redis) Pub/Sub but with Notion System Design</a>
   */
  @Bean
  public JedisConnectionFactory connectionFactory() {
    final RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
    configuration.setHostName(credentials.getHost());
    configuration.setPort(credentials.getPort());
    configuration.setPassword(RedisPassword.of(credentials.getPassword()));
    configuration.setUsername(credentials.getUsername());

    return new JedisConnectionFactory(configuration);
  }

  /**
   * Creates a {@link RedisCacheConfiguration} bean for configuring Redis cache settings.
   *
   * <p>This method sets up the cache configuration with specific settings such as entry time-to-live (TTL),
   * disabling caching of null values, using cache name prefixes, and adding a specific prefix to cache names.</p>
   *
   * @return a configured {@link RedisCacheConfiguration} instance
   * @see <a href="https://velog.io/@dev_hammy/GuideMessaging-with-Redis">
   *   Guide_Messaging with Redis</a>
   */
  @Bean
  public RedisCacheConfiguration redisCacheConfiguration() {
    final RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
    configuration.entryTtl(Duration.ofMinutes(credentials.getTtl()));
    configuration.disableCachingNullValues();
    configuration.usePrefix();
    configuration.prefixCacheNameWith(credentials.getPrefix());

    return configuration;
  }

  /**
   * Provides a {@link StringRedisSerializer} bean for serializing and deserializing Redis keys as Strings.
   * This serializer is used for encoding and decoding Redis keys in the application.
   *
   * @return a configured {@link StringRedisSerializer} instance.
   */
  @Bean
  public StringRedisSerializer stringSerializer() {
    return new StringRedisSerializer();
  }

  /**
   * Creates a {@link GenericJackson2JsonRedisSerializer} bean.
   *
   * <p>This serializer is used to serialize and deserialize Java objects to and from JSON format
   * using the provided {@link ObjectMapper}. It is commonly used for caching objects in Redis
   * where JSON format is preferred.</p>
   *
   * @return a {@link GenericJackson2JsonRedisSerializer} instance
   */
  @Bean
  public GenericJackson2JsonRedisSerializer jackson2JsonSerializer() {
    return new GenericJackson2JsonRedisSerializer(mapper);
  }

  /**
   * Creates a {@link JdkSerializationRedisSerializer} bean.
   *
   * <p>This serializer is used to serialize and deserialize Java objects
   * to and from binary data using Java's built-in serialization mechanism.
   * It is commonly used for caching objects in Redis.</p>
   *
   * @return a {@link JdkSerializationRedisSerializer} instance
   */
  @Bean
  public JdkSerializationRedisSerializer jdkSerializer() {
    return new JdkSerializationRedisSerializer();
  }

  /**
   * Creates a primary {@link RedisTemplate} bean for interacting with Redis.
   *
   * <p>This method sets up a Redis template with specific serializers for keys, values, and hash values.
   * It also configures the connection pool using the provided {@link JedisConnectionFactory}.</p>
   *
   * @param connectionFactory the {@link JedisConnectionFactory} used to establish the Redis connection
   * @return a configured {@link RedisTemplate} instance
   * @see <a href="https://brunch.co.kr/@springboot/695">
   *   Spring WebSocket & Stomp</a>
   */
  @Bean
  @Primary
  public RedisTemplate<String, Object> redisTemplate(final JedisConnectionFactory connectionFactory) {
    final RedisTemplate<String, Object> template = new RedisTemplate<>();
    configurePool(connectionFactory);
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(stringSerializer());
    template.setValueSerializer(jackson2JsonSerializer());
    template.setHashValueSerializer(jdkSerializer());

    return template;
  }

  /**
   * Configures the connection pool settings for the provided {@link JedisConnectionFactory}.
   *
   * <p>This method sets the maximum total and maximum idle connections for the connection pool
   * based on the credentials configuration. If the connection factory's pool configuration
   * is not null, it updates the max total and max idle settings.</p>
   *
   * @param connectionFactory the {@link JedisConnectionFactory} instance to configure
   */
  private void configurePool(final JedisConnectionFactory connectionFactory) {
    if (connectionFactory.getPoolConfig() != null) {
      connectionFactory.getPoolConfig().setMaxTotal(credentials.getMaxTotal());
      connectionFactory.getPoolConfig().setMaxIdle(credentials.getMaxIdle());
    }
  }

  /**
   * Creates a {@link RedisCacheManager} bean for managing Redis caches.
   *
   * <p>This method sets up a RedisCacheManager with the default cache configuration and
   * transaction awareness. It uses the connection factory to establish the Redis connection.</p>
   *
   * @return a configured {@link RedisCacheManager} instance
   * @see <a href="https://velog.io/@ktf1686/Spring-Redis-PUBSUB-WebSocket%EC%9D%84-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EC%B1%84%ED%8C%85-%EC%84%9C%EB%B2%84-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0">
   *   [Spring] Implementing a chat server using Redis PUB/SUB + WebSocket - Simple</a>
   */
  @Bean
  public RedisCacheManager cacheManager() {
    return RedisCacheManager
            .builder(connectionFactory())
            .cacheDefaults(redisCacheConfiguration())
            .transactionAware()
            .build();
  }

  /**
   * Creates a {@link RedisMessageListenerContainer} bean for managing Redis message listeners.
   *
   * <p>This method sets up a RedisMessageListenerContainer with the specified connection factory and
   * message listener adapter. It configures the container to listen to messages on the provided
   * channel topic.</p>
   *
   * @param connectionFactory the connection factory for establishing the Redis connection
   * @param messageListenerAdapter the message listener adapter for handling incoming messages
   * @return a configured {@link RedisMessageListenerContainer} instance
   * @see <a href="https://velog.io/@wwlee94/Redis-PubSub-Base-Server-Sent-Event">
   *   Redis Pub/Sub based SSE (Server-Sent Events) real-time notification application</a>
   */
  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(
      final RedisConnectionFactory connectionFactory, final MessageListenerAdapter messageListenerAdapter) {
    final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.addMessageListener(messageListenerAdapter, channelTopic());
    return container;
  }

  /**
   * Bean definition for a Redis ChannelTopic with the name "stream-event".
   *
   * @return a ChannelTopic instance named "stream-event".
   */
  @Bean("stream-event")
  public ChannelTopic channelTopic() {
    // Return a new ChannelTopic instance with the name "stream-event"
    return ChannelTopic.of("stream-event");
  }

  /**
   * Creates a {@link MessageListenerAdapter} bean for handling incoming messages.
   *
   * <p>This method sets up a MessageListenerAdapter that delegates message handling to the
   * specified {@link StreamEventSubscriber}. It uses the "onMessage" method of the subscriber
   * to process the messages.</p>
   *
   * @param subscription the {@link StreamEventSubscriber} instance that processes incoming messages
   * @return a configured {@link MessageListenerAdapter} instance
   * @see <a href="https://velog.io/@leeseunghee00/Spring-%EC%B1%84%ED%8C%85-%EA%B5%AC%ED%98%84-STOMP-Redis-PubSub">
   *   Spring Real-time Chat Implementation (STOMP & Redis Pub/Sub)</a>
   */
  @Bean
  public MessageListenerAdapter messageListenerAdapter(final StreamEventSubscriber subscription) {
    return new MessageListenerAdapter(subscription, "onMessage");
  }
}
