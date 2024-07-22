package com.fleencorp.feen.repository.event;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EmitterRepository {

  private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  public List<SseEmitter> getEmitters(Object userId) {
    List<SseEmitter> userEmitters = new ArrayList<>();
    emitters.forEach(
        (id, sseEmitter) -> {
          if (id.startsWith(userId.toString())) {
            userEmitters.add(sseEmitter);
          }
        }
    );

    return userEmitters;
  }

  public void addEmitter(Object userId, SseEmitter sseEmitter) {
    emitters.put(userId.toString(), sseEmitter);
  }

  public void removeEmitter(Object userId) {
    emitters.remove(userId.toString());
  }
}
