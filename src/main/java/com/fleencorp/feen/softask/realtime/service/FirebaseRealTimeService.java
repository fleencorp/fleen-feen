package com.fleencorp.feen.softask.realtime.service;

import com.fleencorp.feen.softask.realtime.model.Counters;

import java.util.Map;

public interface FirebaseRealTimeService {

  void pushCounters(Long softAskId, Counters counters);

  void pushReply(Long softAskId, Map<String, Object> replyData);
}
