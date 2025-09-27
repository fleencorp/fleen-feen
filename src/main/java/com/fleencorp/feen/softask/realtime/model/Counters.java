package com.fleencorp.feen.softask.realtime.model;

import com.fleencorp.feen.softask.realtime.constant.SoftAskFields;
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

  private long bookmarkCount;
  private long replyCount;
  private long shareCount;
  private long voteCount;

  public Map<String, Object> toMap() {
    return Map.of(
      SoftAskFields.bookmarkCount(), bookmarkCount,
      SoftAskFields.replyCount(), replyCount,
      SoftAskFields.shareCount(), shareCount,
      SoftAskFields.voteCount(), voteCount
    );
  }
}

