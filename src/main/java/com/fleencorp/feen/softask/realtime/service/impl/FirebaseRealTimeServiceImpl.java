package com.fleencorp.feen.softask.realtime.service.impl;

import com.fleencorp.feen.softask.realtime.model.Counters;
import com.fleencorp.feen.softask.realtime.service.FirebaseRealTimeService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FirebaseRealTimeServiceImpl implements FirebaseRealTimeService {

  private final DatabaseReference dbRef;

  public FirebaseRealTimeServiceImpl(final FirebaseApp firebaseApp) {
    this.dbRef = FirebaseDatabase.getInstance(firebaseApp).getReference();
  }

  @Override
  @Async
  public void pushCounters(Long softAskId, Counters counters) {
    DatabaseReference countersRef = dbRef.child("softAsks").child(String.valueOf(softAskId)).child("counters");
    final Map<String, Object> data = counters.toMap();
    countersRef.setValueAsync(data);
  }

  @Override
  @Async
  public void pushReply(Long softAskId, Map<String, Object> replyData) {
    DatabaseReference repliesRef = dbRef.child("softAsks").child(String.valueOf(softAskId)).child("replies");
    String key = repliesRef.push().getKey();
    repliesRef.child(key).setValueAsync(replyData);
  }
}

