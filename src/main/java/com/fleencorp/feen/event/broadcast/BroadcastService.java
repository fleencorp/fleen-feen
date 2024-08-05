package com.fleencorp.feen.event.broadcast;

import com.fleencorp.feen.event.model.stream.EventStreamCreatedResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Service for broadcasting messages to Redis topics.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
public class BroadcastService {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ChannelTopic streamEventChannelTopic;

  /**
   * Constructs a BroadcastService with the given RedisTemplate and ChannelTopic.
   *
   * @param redisTemplate the Redis template used for sending messages.
   * @param streamEventChannelTopic the Redis topic for stream events.
   */
  public BroadcastService(
      final RedisTemplate<String, Object> redisTemplate,
      @Qualifier("stream-event") final ChannelTopic streamEventChannelTopic) {
    this.redisTemplate = redisTemplate;
    this.streamEventChannelTopic = streamEventChannelTopic;
  }

  /**
   * Broadcasts the event creation result to the Redis topic asynchronously.
   *
   * @param eventStreamCreatedResult the result of the event stream creation.
   */
  @Async
  public void broadcastEventCreated(final EventStreamCreatedResult eventStreamCreatedResult) {
    // Convert and send the eventStreamCreatedResult to the stream event channel topic
    redisTemplate.convertAndSend(streamEventChannelTopic.getTopic(), eventStreamCreatedResult);
  }
}
