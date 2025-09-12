package com.fleencorp.feen.softask.realtime.mapper;

import com.fleencorp.feen.common.util.common.DateTimeUtil;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.realtime.model.Counters;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class SoftAskRealtimeDataMapper {

  public Counters recomputeCounters(SoftAsk sa) {
    long like = sa.getVoteCount();
    long bookmark = sa.getBookmarkCount();
    long vote = sa.getVoteCount();
    long reply = sa.getReplyCount();
    return new Counters(like, bookmark, vote, reply);
  }

  public Counters recomputeCounters(SoftAskReply sa) {
    long like = sa.getVoteCount();
    long bookmark = sa.getBookmarkCount();
    long vote = sa.getVoteCount();
    long reply = sa.getChildReplyCount();
    return new Counters(like, bookmark, vote, reply);
  }


  public static Map<String,Object> mapReplyToMap(SoftAskReply r) {
    final String displayTimeLabel = DateTimeUtil.formatTime(r.getCreatedOn());
    // Have avatar URL
    Map<String,Object> m = new HashMap<>();
    m.put("id", r.getId());
    m.put("content", r.getContent());
    m.put("username", r.getUserAliasOrUsername());
    m.put("display_name", r.getUserDisplayName());
    m.put("parent_id", r.getParentId());
    m.put("parent_reply_id", r.getParentReplyId());
    m.put("other_parent_id", r.getOtherParentId());
    m.put("createdAt", r.getCreatedOn().toString());

    m.put("display_time_label", displayTimeLabel);

    return m;
  }
}
