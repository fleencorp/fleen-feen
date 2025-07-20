package com.fleencorp.feen.common.event.subscriber;

import com.fleencorp.base.util.JsonUtil;
import com.fleencorp.feen.common.constant.base.ResultType;
import com.fleencorp.feen.common.event.model.stream.ResultData;
import com.fleencorp.feen.common.repository.event.EmitterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.fleencorp.feen.common.util.LoggingUtil.logIfEnabled;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class StreamEventSubscriber implements MessageListener {

  private final EmitterRepository emitterRepository;
  private final JsonUtil jsonUtil;

  /**
   * Constructs a StreamEventSubscriber with the specified dependencies.
   *
   * @param emitterRepository the repository for managing SseEmitters
   * @param jsonUtil the utility for JSON operations
   */
  public StreamEventSubscriber(
      final EmitterRepository emitterRepository,
      final JsonUtil jsonUtil) {
    this.emitterRepository = emitterRepository;
    this.jsonUtil = jsonUtil;
  }

  /**
   * Handles incoming messages by processing them and updating the system.
   *
   * @param message the message received from the Redis channel.
   * @param pattern the pattern of the channel that the message was received from.
   */
  @Override
  public void onMessage(final Message message, final byte[] pattern) {
    // Convert the message body from byte array to string
    final String body = new String(message.getBody());
    // Deserialize the JSON string into a ResultData object
    final ResultData resultData = getData(body, ResultData.class);
    // Extract the user ID from the ResultData object
    final String userId = resultData.getUserId();
    // Process the result based on the user ID and result data
    processResult(userId, resultData, body);
  }


  /**
   * Retrieves data from a JSON string and converts it to the specified class type.
   *
   * @param value the JSON string.
   * @param clazz the class type to convert the JSON string to.
   * @param <T> the type of the class.
   * @return an object of the specified class type.
   */
  protected <T> T getData(final String value, final Class<T> clazz) {
    // Use the jsonUtil to parse the JSON string and convert it to the specified class type
    return jsonUtil.get(value, clazz);
  }

  /**
   * Sends data to the client through the given list of SSE emitters.
   *
   * @param userId the ID of the user.
   * @param emitters the list of SSE emitters.
   * @param dataWithMediaTypes the data to be sent.
   */
  protected void sendDataToClient(final String userId, final List<SseEmitter> emitters, final Set<ResponseBodyEmitter.DataWithMediaType> dataWithMediaTypes) {
    try {
      // Iterate over the list of emitters and send the data to each one
      for (final SseEmitter emitter : emitters) {
        emitter.send(dataWithMediaTypes);
      }
    } catch (final IOException ex) {
      logIfEnabled(log::isErrorEnabled, () -> log.error("Unable to process request. Reason: {}", ex.getMessage()));
      // If an IOException occurs, remove the emitter associated with the user ID
      emitterRepository.removeEmitter(userId);
    }
  }

  /**
   * Builds an event stream created event.
   *
   * @param id the ID of the entity.
   * @param data the data to be sent with the event.
   * @return a set of DataWithMediaType objects representing the event.
   */
  protected Set<ResponseBodyEmitter.DataWithMediaType> buildEventStreamCreatedEvent(final String id, final Object data) {
    return SseEmitter.event()
        .id(id)
        .data(data)
        .name("stream-event")
        .build();
  }

  protected void processResult(final String id, final ResultData resultData, final String actualData) {
    if (requireNonNull(resultData.getResultType()) == ResultType.EVENT_STREAM_CREATED) {
      final List<SseEmitter> emitters = emitterRepository.getEmitters(resultData.getUserId());
      sendDataToClient(id, emitters, buildEventStreamCreatedEvent(id, actualData));
    }
  }

}
