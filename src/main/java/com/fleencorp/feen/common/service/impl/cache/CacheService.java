package com.fleencorp.feen.common.service.impl.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleencorp.base.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.fleencorp.feen.common.util.LoggingUtil.logIfEnabled;
import static java.util.Objects.nonNull;

/**
 * CacheService provides methods for interacting with a Redis cache.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Component
public class CacheService {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper mapper;
  private final JsonUtil jsonUtil;
  private final Map<String, Object> objectsMap = new HashMap<>();

  /**
   * Constructs a new CacheService with the given RedisTemplate and ObjectMapper.
   *
   * @param redisTemplate The RedisTemplate to use for cache operations.
   * @param mapper        The ObjectMapper to use for serialization/deserialization.
   */
  public CacheService(
      final RedisTemplate<String, Object> redisTemplate,
      final ObjectMapper mapper,
      final JsonUtil jsonUtil) {
    this.redisTemplate = redisTemplate;
    this.mapper = mapper;
    this.jsonUtil = jsonUtil;
  }

  /**
   * Checks if the specified key exists in the cache.
   *
   * @param key The key to check.
   * @return true if the key exists, false otherwise.
   */
  public boolean exists(final String key) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

  /**
   * Retrieves the value associated with the specified key from the cache.
   *
   * @param key The key to retrieve the value for.
   * @return The value associated with the key, or null if the key does not exist.
   */
  public Object get(final String key) {
    return redisTemplate.opsForValue().get(key);
  }

  /**
   * Retrieves the value associated with the specified hash and key from the cache.
   *
   * @param hash The hash to retrieve the value from.
   * @param key  The key within the hash to retrieve the value for.
   * @return The value associated with the key within the hash, or null if the key or hash does not exist.
   */
  public Object getByHash(final String hash, final String key) {
    return redisTemplate.opsForHash().get(key, hash);
  }

  /**
   * Sets the specified key in the cache to the given value.
   *
   * @param key   The key to set.
   * @param value The value to set for the key.
   */
  public void set(final String key, final String value) {
    redisTemplate.opsForValue().set(key, value);
  }

  /**
   * Sets the specified key in the cache to the given value with an expiration time.
   *
   * @param key      The key to set.
   * @param value    The value to set for the key.
   * @param duration The duration after which the key will expire.
   */
  public void set(final String key, final String value, final Duration duration) {
    set(key, value);
    expire(key, duration);
  }

  /**
   * Sets the value associated with the specified key and hash in the cache.
   *
   * @param hash  The hash to set the value in.
   * @param key   The key within the hash to set.
   * @param value The value to set for the key within the hash.
   */

  public void setByHash(final String hash, final String key, final Object value) {
    redisTemplate.opsForHash().put(hash, key, value);
  }

  /**
   * Sets the expiration time for the specified key in the cache.
   *
   * @param key      The key to set the expiration time for.
   * @param duration The duration after which the key will expire.
   */
  public void expire(final String key, final Duration duration) {
    redisTemplate.expire(key, duration);
  }

  /**
   * Deletes the specified key from the cache.
   *
   * @param key The key to delete.
   */
  public void delete(final String key) {
    redisTemplate.delete(key);
  }

  /**
   * Deletes the value associated with the specified key and hash from the cache.
   *
   * @param hash The hash to delete the value from.
   * @param key  The key within the hash to delete.
   */
  public void deleteByHash(final String hash, final String key) {
    redisTemplate.opsForHash().delete(key, hash);
  }

  /**
   * Checks if the cache entry exists, and if so, deletes it.
   */
  public void existsAndDelete(final String cacheKey) {
    if (exists(cacheKey)) {
      delete(cacheKey);
    }
  }

  /**
   * Sets the specified key in the cache to the serialized JSON representation of the given value.
   *
   * @param key   The key to set.
   * @param value The value to serialize and set for the key.
   */
  public void set(final String key, final Object value) {
    if (nonNull(key) && nonNull(value)) {
      try {
        final String jsonString = mapper.writeValueAsString(value);
        set(key, jsonString);
      } catch (final JsonProcessingException ex) {
        logIfEnabled(log::isErrorEnabled, () -> log.error(ex.getMessage(), ex));
      }
    }
  }

  /**
   * Retrieves the value associated with the specified key from the cache and deserializes it into an object of the given class.
   *
   * @param key   The key to retrieve the value for.
   * @param clazz The class type of the value to deserialize.
   * @param <T>   The type of the value to deserialize.
   * @return The deserialized value associated with the key,
   * or null if the key does not exist or the value could not be deserialized.
   */
  public <T> T get(final String key, final Class<T> clazz) {
    final String value = (String) get(key);
    if (nonNull(value) && nonNull(key) && nonNull(clazz)) {
      return jsonUtil.get(value, clazz);
    }
    return null;
  }

}
