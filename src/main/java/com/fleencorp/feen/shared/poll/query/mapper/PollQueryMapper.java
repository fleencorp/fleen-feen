package com.fleencorp.feen.shared.poll.query.mapper;

import com.fleencorp.feen.shared.poll.model.PollData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class PollQueryMapper implements RowMapper<PollData> {

  private PollQueryMapper() {}

  public static PollQueryMapper of() {
    return new PollQueryMapper();
  }

  @Override
  public PollData mapRow(ResultSet rs, int rowNum) throws SQLException {
    final PollData poll = new PollData();

    Long pollId = rs.getLong("pollId");
    String question = rs.getString("question");
    String description = rs.getString("description");
    Long authorId = rs.getLong("authorId");
    Long parentId = rs.getLong("parentId");
    String parentTitle = rs.getString("parentTitle");
    Long streamId = rs.getLong("streamId");
    Long chatSpaceId = rs.getLong("chatSpaceId");

    poll.setPollId(pollId);
    poll.setQuestion(question);
    poll.setDescription(description);
    poll.setAuthorId(authorId);
    poll.setParentId(parentId);
    poll.setParentTitle(parentTitle);
    poll.setStreamId(streamId);
    poll.setChatSpaceId(chatSpaceId);

    return poll;
  }
}

