package com.fleencorp.feen.softask.realtime.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Counters {

  public long likeCount;
  public long bookmarkCount;
  public long voteCount;
  public long replyCount;

  public Map<String, Object> toMap() {
    return Map.of(
      "like_count", likeCount,
      "bookmark_count", bookmarkCount,
      "vote_count", voteCount,
      "reply_count", replyCount
    );
  }
}

