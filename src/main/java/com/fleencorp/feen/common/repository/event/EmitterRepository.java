package com.fleencorp.feen.common.repository.event;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @see <a href="https://velog.io/@bsangyong93/SSE%EB%A1%9C-%EC%95%8C%EB%A6%BC-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-feat.Spring-boot">
 *   Implementing notification functionality with SSE - feat.Spring boot</a>
 */
@Component
public class EmitterRepository {

  private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  public List<SseEmitter> getEmitters(final Object userId) {
    final List<SseEmitter> userEmitters = new ArrayList<>();
    emitters.forEach(
        (id, sseEmitter) -> {
          if (id.startsWith(userId.toString())) {
            userEmitters.add(sseEmitter);
          }
        }
    );

    return userEmitters;
  }

  public void addEmitter(final Object userId, final SseEmitter sseEmitter) {
    emitters.put(userId.toString(), sseEmitter);
  }

  public void removeEmitter(final Object userId) {
    emitters.remove(userId.toString());
  }
}
